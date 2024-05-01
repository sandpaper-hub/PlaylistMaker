package com.practicum.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.model.TracksState

class TracksSearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CHECK_TEXT_DELAY = 50L
    }

    var isCreated = false
    var lastSearchText: String = ""
    private var isClickAllowed = true

    private val stateLiveData = MutableLiveData<TracksState>()

    private val handler = Handler(Looper.getMainLooper())

    fun observeState(): LiveData<TracksState> {
        isCreated = true
        return stateLiveData
    }

    fun searchDebounce(changedText: String?) {
        if (changedText!!.isEmpty()) {
            val historyTrackList = tracksInteractor.getHistory()
            renderState(TracksState.HistoryContent(historyTrackList))
        } else {
            renderState(TracksState.Empty)
        }
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        lastSearchText = changedText
    }

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText
        searchRequest(newSearchText)
    }

    fun init() {
        showHistory()
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                        when {
                            tracks.isEmpty() -> {
                                renderState(TracksState.NothingFound)
                            }

                            else -> renderState(TracksState.Content(tracks))
                        }
                    } else {
                        renderState(TracksState.ConnectionError)
                    }
                }
            })
        }
    }

    fun showHideClearEditTextButton(text: String) {
        handler.postDelayed({ renderState(TracksState.ClearedEditText(text)) }, CHECK_TEXT_DELAY)

    }

    fun addTrackToHistory(track: Track) {
        tracksInteractor.addTrackToHistory(track)
    }

    fun clearHistory() {
        tracksInteractor.clearHistory()
        renderState(TracksState.Empty)
    }

    private fun showHistory() {
        val historyTrackList = tracksInteractor.getHistory()
        if (historyTrackList.isEmpty()) {
            renderState(TracksState.Empty)
        } else {
            renderState(TracksState.HistoryContent(historyTrackList))
        }
    }

    fun getHistory(): ArrayList<Track> {
        return tracksInteractor.getHistory()
    }

    fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacks(searchRunnable)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, SearchActivity.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}