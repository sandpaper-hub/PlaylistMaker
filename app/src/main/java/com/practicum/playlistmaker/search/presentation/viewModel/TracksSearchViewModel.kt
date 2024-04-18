package com.practicum.playlistmaker.search.presentation.viewModel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.presentation.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class TracksSearchViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { TracksSearchViewModel(this[APPLICATION_KEY] as Application) }
        }
    }

    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())
    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private var historyTrackList: ArrayList<Track> = tracksInteractor.getHistory()

    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    fun searchDebounce(changedText: String): String {
        if (lastSearchText == changedText) {
            return lastSearchText ?: ""
        }
        if (changedText.isEmpty()) {
            historyTrackList = tracksInteractor.getHistory()
            renderState(TracksState.HistoryContent(historyTrackList))
        } else {
            renderState(TracksState.Empty)
        }
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        lastSearchText = changedText
        return lastSearchText ?: ""
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
                                renderState(
                                    TracksState.ConnectionError(
                                        getApplication<Application>().getString(R.string.connection_error),
                                        AppCompatResources.getDrawable(
                                            getApplication(),
                                            R.drawable.bad_connection_image
                                        )
                                    )
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    TracksState.NothingFound(
                                        getApplication<Application>().getString(R.string.nothing_found),
                                        AppCompatResources.getDrawable(
                                            getApplication(),
                                            R.drawable.nothing_found_image
                                        )
                                    )
                                )
                            }

                            else -> renderState(TracksState.Content(tracks))
                        }
                    }
                }

            })
        }
    }

    fun showHideClearEditTextButton(text: String) {
        renderState(TracksState.ClearedEditText(text))
    }

    fun addTrackToHistory(track: Track) {
        tracksInteractor.addTrackToHistory(track)
    }

    fun clearHistory() {
        tracksInteractor.clearHistory()
        renderState(TracksState.Empty)
    }

    fun showHistory() {
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
        Log.d("SAMPLE1",state.toString())
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacks(searchRunnable)
    }
}