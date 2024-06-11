package com.practicum.playlistmaker.search.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.model.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TracksSearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CHECK_TEXT_DELAY = 50L
    }

    var lastSearchText: String = ""

    private val stateLiveData = MutableLiveData<TracksState>()

    init {
        showHistory()
    }

    private var debounceSearchJob: Job? = null
    private var showHideClearEditTextJob: Job? = null

    fun observeState(): LiveData<TracksState> {
        return stateLiveData
    }

    fun searchDebounce(changedText: String?) {
        if (changedText!!.isEmpty()) {
            val historyTrackList = tracksInteractor.getHistory()
            if (historyTrackList.isEmpty()) {
                renderState(TracksState.Empty)
            } else {
                renderState(TracksState.HistoryContent(historyTrackList))
            }
        } else {
            renderState(TracksState.Empty)
        }

        lastSearchText = changedText
        debounceSearchJob?.cancel()
        debounceSearchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }

    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor.searchTracks((newSearchText)).collect{pair ->
                    val tracks = mutableListOf<Track>()
                    if (pair.first != null) {
                        tracks.addAll(pair.first!!)
                    }

                    when {
                        pair.second != null -> renderState(TracksState.ConnectionError)
                        tracks.isEmpty() -> renderState(TracksState.NothingFound)
                        else -> renderState(TracksState.Content(tracks))
                    }
                }
            }
        }
    }

    fun showHideClearEditTextButton(text: String) {
        showHideClearEditTextJob = viewModelScope.launch {
            delay(CHECK_TEXT_DELAY)
            renderState(TracksState.ClearedEditText(text))
        }
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
}