package com.practicum.playlistmaker.search.presentation.viewModel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.presentation.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchActivity

class TracksSearchViewModel : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CHECK_TEXT_DELAY = 50L
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { TracksSearchViewModel() }
        }
    }

    var isCreated = false
    var lastSearchText: String = ""
    private var isClickAllowed = true

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val stateLiveData = MutableLiveData<TracksState>()

    private val handler = Handler(Looper.getMainLooper())

    fun observeState(): LiveData<TracksState> = stateLiveData

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

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                        when {
                            errorMessage != null -> {
                                renderState(TracksState.ConnectionError)
                            }

                            tracks.isEmpty() -> {
                                renderState(TracksState.NothingFound)
                            }

                            else -> renderState(TracksState.Content(tracks))
                        }
                    }
                }

            })
        }
    }

    fun showHideClearEditTextButton(text: String) {
        handler.postDelayed({renderState(TracksState.ClearedEditText(text))}, CHECK_TEXT_DELAY)

    }

    fun addTrackToHistory(track: Track) {
        tracksInteractor.addTrackToHistory(track)
    }

    fun clearHistory() {
        tracksInteractor.clearHistory()
        renderState(TracksState.Empty)
    }

    fun showHistory() {
        val historyTrackList = tracksInteractor.getHistory()
        if (historyTrackList.isEmpty()) {
            renderState(TracksState.Empty)
        } else {
            isCreated = true
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