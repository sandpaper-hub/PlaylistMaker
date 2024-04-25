package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.GlobalConstants
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.util.hasNullableData
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.presentation.viewModel.TracksState
import com.practicum.playlistmaker.search.presentation.viewModel.TracksSearchViewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding

    private lateinit var viewModel: TracksSearchViewModel

    private lateinit var trackListAdapter: TrackListAdapter

    private lateinit var historyAdapter: TrackListAdapter
    private lateinit var mainHandler: Handler
    private var sharedPreferences: SharedPreferences? = null

    private var textWatcher: TextWatcher? = null

    private var onSharedPreferencesChangeListener: OnSharedPreferenceChangeListener? = null


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            TracksSearchViewModel.getViewModelFactory()
        )[TracksSearchViewModel::class.java]
        viewModel.observeState().observe(this) {
            render(it)
        }

        mainHandler = Handler(Looper.getMainLooper())
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
            getSharedPreferences(
                GlobalConstants.SHARED_PREFERENCES_HISTORY_FILE,
                MODE_PRIVATE
            )

        onSharedPreferencesChangeListener =
            OnSharedPreferenceChangeListener { _, key ->
                if (key == GlobalConstants.NEW_HISTORY_ITEM_KEY) {
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
                        val playerIntent = Intent(applicationContext, PlayerActivity::class.java)
                        playerIntent.putExtra(GlobalConstants.INTENT_EXTRA_KEY, track)
                        startActivity(playerIntent)
                    }
                }
            })

        binding.historyRecycler.adapter = historyAdapter
        if (!viewModel.isCreated) {
            viewModel.showHistory()
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        trackListAdapter =
            TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (!track.hasNullableData()) {
                        if (viewModel.clickDebounce()) {
                            viewModel.addTrackToHistory(track)
                            val playerIntent =
                                Intent(applicationContext, PlayerActivity::class.java)
                            playerIntent.putExtra(
                                GlobalConstants.INTENT_EXTRA_KEY,
                                track
                            )
                            startActivity(playerIntent)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Track has empty data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

        binding.trackListRecyclerView.adapter = trackListAdapter

        binding.backButtonSearchActivity.setOnClickListener {
            finish()
        }

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
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
    private fun showContent(tracks: List<Track>) = with(binding){
        trackListRecyclerView.visibility = View.VISIBLE

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