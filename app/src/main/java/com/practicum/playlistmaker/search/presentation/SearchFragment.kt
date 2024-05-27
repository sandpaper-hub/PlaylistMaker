package com.practicum.playlistmaker.search.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.presentation.PlayerFragment
import com.practicum.playlistmaker.search.NEW_HISTORY_ITEM_KEY
import com.practicum.playlistmaker.search.SHARED_PREFERENCES_HISTORY_FILE
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.model.TracksState
import com.practicum.playlistmaker.util.hasNullableData
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: FragmentSearchBinding

    private val viewModel by viewModel<TracksSearchViewModel>()

    private lateinit var trackListAdapter: TrackListAdapter

    private lateinit var historyAdapter: TrackListAdapter
    private var sharedPreferences: SharedPreferences? = null

    private var textWatcher: TextWatcher? = null

    private var onSharedPreferencesChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }


        binding.searchEditText.setText(viewModel.lastSearchText)

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(s?.toString() ?: "")
                viewModel.showHideClearEditTextButton(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        textWatcher.let { binding.searchEditText.addTextChangedListener(it) }

        sharedPreferences =
            requireContext().getSharedPreferences(
                SHARED_PREFERENCES_HISTORY_FILE,
                AppCompatActivity.MODE_PRIVATE
            )

        onSharedPreferencesChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == NEW_HISTORY_ITEM_KEY) {
                    historyAdapter.trackList = viewModel.getHistory()
                    historyAdapter.notifyDataSetChanged()
                }
            }

        onSharedPreferencesChangeListener.let {
            sharedPreferences?.registerOnSharedPreferenceChangeListener(it)
        }

        historyAdapter =
            TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (viewModel.clickDebounce()) {
                        findNavController().navigate(
                            R.id.action_searchFragment_to_playerFragment,
                            PlayerFragment.createArgs(track)
                        )
                    }
                }
            })

        binding.historyRecycler.adapter = historyAdapter

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        trackListAdapter =
            TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (!track.hasNullableData()) {
                        if (viewModel.clickDebounce()) {
                            viewModel.addTrackToHistory(track)
                            findNavController().navigate(
                                R.id.action_searchFragment_to_playerFragment,
                                PlayerFragment.createArgs(track)
                            )
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Track has empty data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

        binding.trackListRecyclerView.adapter = trackListAdapter

        binding.searchEditText.setOnEditorActionListener { editTextView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editTextView.text.isNotEmpty()) {
                    viewModel.searchDebounce(editTextView.text.toString())
                }
            }
            false
        }

        binding.clearSearchEdiText.setOnClickListener {
            binding.searchEditText.setText("")
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.refreshSearchButton.setOnClickListener {
            viewModel.searchDebounce(viewModel.lastSearchText)
        }
    }

    override fun onPause() {
        super.onPause()
        onSharedPreferencesChangeListener.let {
            sharedPreferences?.unregisterOnSharedPreferenceChangeListener(
                it
            )
        }
    }

    override fun onResume() {
        super.onResume()
        onSharedPreferencesChangeListener.let {
            sharedPreferences?.registerOnSharedPreferenceChangeListener(
                it
            )
        }
    }

    private fun render(state: TracksState) { //ok
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.ConnectionError -> showConnectionError()

            is TracksState.NothingFound -> showEmpty()
            is TracksState.ClearedEditText -> showHideClearEditTextButton(state.text)
            is TracksState.HistoryContent -> showHistory(state.tracks)
            is TracksState.Empty -> showEmptyState()
        }
    }

    private fun showLoading() = with(binding) {
        searchProgressBar.visibility = View.VISIBLE

        badSearchResultGroup.visibility = View.GONE
        historySearchContainer.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
        connectionErrorGroup.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) = with(binding) {
        trackListRecyclerView.visibility = View.VISIBLE
        clearSearchEdiText.visibility = View.VISIBLE

        searchProgressBar.visibility = View.GONE
        historySearchContainer.visibility = View.GONE
        connectionErrorGroup.visibility = View.GONE

        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun showConnectionError() = with(binding) {
        connectionErrorGroup.visibility = View.VISIBLE
        badSearchResultText.setText(R.string.connection_error)
        badSearchResultImage.setImageResource(R.drawable.bad_connection_image)

        trackListRecyclerView.visibility = View.GONE
        searchProgressBar.visibility = View.GONE
        historySearchContainer.visibility = View.GONE
    }

    private fun showEmpty() = with(binding) {
        badSearchResultText.setText(R.string.nothing_found)
        badSearchResultImage.setImageResource(R.drawable.nothing_found_image)
        badSearchResultGroup.visibility = View.VISIBLE

        trackListRecyclerView.visibility = View.GONE
        searchProgressBar.visibility = View.GONE
        historySearchContainer.visibility = View.GONE
    }

    private fun showHideClearEditTextButton(text: String) = with(binding) {
        if (text.isEmpty()) {
            clearSearchEdiText.visibility = View.GONE
            trackListRecyclerView.visibility = View.GONE
        } else {
            clearSearchEdiText.visibility = View.VISIBLE
        }
    }

    private fun showEmptyState() = with(binding) {
        historySearchContainer.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory(historyTrackList: List<Track>) = with(binding) {
        historySearchContainer.visibility = View.VISIBLE
        connectionErrorGroup.visibility = View.GONE
        trackListRecyclerView.visibility = View.GONE

        historyAdapter.trackList.clear()
        historyAdapter.trackList.addAll(historyTrackList)
        historyAdapter.notifyDataSetChanged()
    }
}