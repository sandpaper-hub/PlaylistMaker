package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.SharedPreferencesData
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.hasNullableData
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.TracksState
import com.practicum.playlistmaker.search.presentation.TracksSearchPresenter
import com.practicum.playlistmaker.search.presentation.TracksView

class SearchActivity : AppCompatActivity(), TracksView {

    companion object {
        const val INSTANCE_STATE_KEY = "SAVED_RESULT"
        const val INTENT_EXTRA_KEY = "selectedTrack"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding

    var savedText = ""
    private var restoredText = ""
    private var lastSearchText: String = ""

    private var isClickAllowed = true

    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var historyAdapter: TrackListAdapter

    private lateinit var mainHandler: Handler
    private var sharedPreferences: SharedPreferences? = null
    private var textWatcher: TextWatcher? = null
    private var tracksSearchPresenter: TracksSearchPresenter? = null

    @SuppressLint("NotifyDataSetChanged")
    private var onSharedPreferencesChangeListener: OnSharedPreferenceChangeListener? = null

    override fun onStart() {
        super.onStart()
        tracksSearchPresenter?.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        tracksSearchPresenter?.attachView(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tracksSearchPresenter = (this.applicationContext as? App)?.tracksSearchPresenter
        if (tracksSearchPresenter == null) {
            tracksSearchPresenter = Creator.provideTracksSearchPresenter(this.applicationContext)
            (this.application as? App)?.tracksSearchPresenter = tracksSearchPresenter
        }

        mainHandler = Handler(Looper.getMainLooper())

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastSearchText = tracksSearchPresenter?.searchDebounce(s?.toString() ?: "") ?: ""
            }

            override fun afterTextChanged(s: Editable?) {
                savedText = binding.searchEditText.text.toString()
            }
        }

        textWatcher.let { binding.searchEditText.addTextChangedListener(it) }

        sharedPreferences =
            getSharedPreferences(
                SharedPreferencesData.SHARED_PREFERENCES_HISTORY_FILE,
                MODE_PRIVATE
            )

        onSharedPreferencesChangeListener =
            OnSharedPreferenceChangeListener { _, key ->
                if (key == SharedPreferencesData.NEW_HISTORY_ITEM_KEY) {
                    historyAdapter.trackList = tracksSearchPresenter!!.getHistory()
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
                        playerIntent.putExtra(INTENT_EXTRA_KEY, track)
                        startActivity(playerIntent)
                    }
                }
            })

        binding.historyRecycler.adapter = historyAdapter
        tracksSearchPresenter?.showHistory()

        binding.clearHistory.setOnClickListener {
            tracksSearchPresenter?.clearHistory()
        }

        trackListAdapter =
            TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
                override fun onItemClick(track: Track) {
                    if (!track.hasNullableData()) {
                        if (clickDebounce()) {
                            tracksSearchPresenter?.addTrackToHistory(track)
                            val playerIntent =
                                Intent(applicationContext, PlayerActivity::class.java)
                            playerIntent.putExtra(
                                INTENT_EXTRA_KEY,
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
            onBackPressedDispatcher.onBackPressed()
        }

        binding.searchEditText.isSaveEnabled = false
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->  //ok
            tracksSearchPresenter?.showHideClearEditTextButton(text.toString())
        }

        binding.searchEditText.setOnEditorActionListener { editTextView, actionId, _ -> //ok
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editTextView.text.isNotEmpty()) {
                    lastSearchText =
                        tracksSearchPresenter?.searchDebounce(editTextView.text.toString()) ?: ""
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
            lastSearchText = tracksSearchPresenter?.searchDebounce(lastSearchText) ?: ""
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { //остаётся
        super.onRestoreInstanceState(savedInstanceState)
        restoredText = savedInstanceState.getString(INSTANCE_STATE_KEY).toString()
        binding.searchEditText.setText(restoredText)
    }

    override fun onSaveInstanceState(outState: Bundle) { //остаётся
        super.onSaveInstanceState(outState)
        tracksSearchPresenter?.detachView()
        outState.putString(INSTANCE_STATE_KEY, savedText)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { binding.searchEditText.removeTextChangedListener(it) }
        onSharedPreferencesChangeListener.let {
            sharedPreferences?.unregisterOnSharedPreferenceChangeListener(
                it
            )
        }
        tracksSearchPresenter?.onDestroy()
        if (isFinishing) {
            tracksSearchPresenter = null
        }
    }

    override fun onPause() {
        super.onPause()
        tracksSearchPresenter?.detachView()
    }

    override fun onStop() {
        super.onStop()
        tracksSearchPresenter?.detachView()
    }

    private fun clickDebounce(): Boolean { //остаётся
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun render(state: TracksState) { //ok
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.ConnectionError -> showConnectionError(
                state.errorMessage,
                state.drawable
            )

            is TracksState.NothingFound -> showEmpty(state.message, state.drawable)
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

    private fun showConnectionError(errorMessage: String, drawable: Drawable?) { //ok
        binding.connectionErrorGroup.visibility = View.VISIBLE
        binding.badSearchResultText.text = errorMessage
        binding.badSearchResultImage.setImageDrawable(drawable)

        binding.trackListRecyclerView.visibility = View.GONE
        binding.searchProgressBar.visibility = View.GONE
        binding.historySearchContainer.visibility = View.GONE
    }

    private fun showEmpty(errorMessage: String, drawable: Drawable?) { //ok
        binding.badSearchResultText.text = errorMessage
        binding.badSearchResultImage.setImageDrawable(drawable)
        binding.badSearchResultGroup.visibility = View.VISIBLE
        Log.d("RENDERING_STATE", binding.badSearchResultImage.visibility.toString())

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

    private fun showEmptyState() {
        binding.historySearchContainer.visibility = View.GONE
        binding.trackListRecyclerView.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory(historyTrackList: List<Track>) {
        binding.historySearchContainer.visibility = View.VISIBLE
        binding.connectionErrorGroup.visibility = View.GONE
        binding.trackListRecyclerView.visibility = View.GONE

        historyAdapter.trackList.clear()
        historyAdapter.trackList.addAll(historyTrackList)
        historyAdapter.notifyDataSetChanged()

    }
}