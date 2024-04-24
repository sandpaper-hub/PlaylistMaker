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
        const val INSTANCE_STATE_KEY = "SAVED_RESULT"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val CHECK_TEXT_DELAY = 200L
    }

    private lateinit var binding: ActivitySearchBinding

    private lateinit var viewModel: TracksSearchViewModel

    var savedText = ""
    private var restoredText = ""

    private var lastSearchText: String = ""

    private var isClickAllowed = true
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

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastSearchText = viewModel.searchDebounce(s?.toString() ?: "")
                mainHandler.postDelayed(
                    { viewModel.showHideClearEditTextButton(s.toString()) },
                    CHECK_TEXT_DELAY
                )
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = binding.searchEditText.text.toString()
            }
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
                    if (clickDebounce()) {
                        val playerIntent = Intent(applicationContext, PlayerActivity::class.java)
                        playerIntent.putExtra(GlobalConstants.INTENT_EXTRA_KEY, track)
                        startActivity(playerIntent)
                    }
                }
            })

        binding.historyRecycler.adapter = historyAdapter
        viewModel.showHistory()

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        trackListAdapter =
            TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (!track.hasNullableData()) {
                        if (clickDebounce()) {
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

        binding.backButtonSearchActivity.setOnClickListener {//ok
            finish()
        }

        binding.searchEditText.isSaveEnabled = false

        binding.searchEditText.setOnEditorActionListener { editTextView, actionId, _ -> //ok
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editTextView.text.isNotEmpty()) {
                    lastSearchText =
                        viewModel.searchDebounce(editTextView.text.toString())
                }
            }
            false
        }

        binding.clearSearchEdiText.setOnClickListener {//ok
            binding.searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.refreshSearchButton.setOnClickListener {//ok
            lastSearchText = viewModel.searchDebounce(lastSearchText)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { //остаётся
        super.onRestoreInstanceState(savedInstanceState)
        restoredText = savedInstanceState.getString(INSTANCE_STATE_KEY).toString()
        binding.searchEditText.setText(restoredText)
    }

    override fun onSaveInstanceState(outState: Bundle) { //остаётся
        super.onSaveInstanceState(outState)
        outState.putString(INSTANCE_STATE_KEY, savedText)
    }

    private fun clickDebounce(): Boolean { //остаётся
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

    private fun showLoading() { //ok
        binding.searchProgressBar.visibility = View.VISIBLE

        binding.badSearchResultGroup.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
        binding.trackListRecyclerView.visibility = View.GONE
        binding.connectionErrorGroup.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) { //ok
        binding.trackListRecyclerView.visibility = View.VISIBLE

        binding.searchProgressBar.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
        binding.connectionErrorGroup.visibility = View.GONE

        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun showConnectionError() { //ok
        binding.connectionErrorGroup.visibility = View.VISIBLE
        binding.badSearchResultText.setText(R.string.connection_error)
        binding.badSearchResultImage.setImageResource(R.drawable.bad_connection_image)

        binding.trackListRecyclerView.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
    }

    private fun showEmpty() { //ok
        binding.badSearchResultText.setText(R.string.nothing_found)
        binding.badSearchResultImage.setImageResource(R.drawable.nothing_found_image)
        binding.badSearchResultGroup.visibility = View.VISIBLE

        binding.trackListRecyclerView.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
    }

    private fun showHideClearEditTextButton(text: String) {
        if (text.isEmpty()) {
            binding.clearSearchEdiText.visibility = View.GONE
            binding.trackListRecyclerView.visibility = View.GONE
        } else {
            binding.clearSearchEdiText.visibility = View.VISIBLE
        }
    }

    private fun showEmptyState() { // HERE
        binding.historySearchContainer.visibility = View.GONE
        binding.trackListRecyclerView.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory(historyTrackList: List<Track>) { //HERE
        binding.historySearchContainer.visibility = View.VISIBLE
        binding.connectionErrorGroup.visibility = View.GONE
        binding.trackListRecyclerView.visibility = View.GONE

        historyAdapter.trackList.clear()
        historyAdapter.trackList.addAll(historyTrackList)
        historyAdapter.notifyDataSetChanged()

    }
}