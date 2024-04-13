package com.practicum.playlistmaker.search.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.appcompat.content.res.AppCompatResources
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.TracksState

class TracksSearchPresenter(private val view: TracksView, private val context: Context) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val trackList: ArrayList<Track> = ArrayList()

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    fun searchDebounce(changedText: String): String {
        lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        return lastSearchText ?: ""
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            view.render(TracksState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        if (foundTracks != null) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                        }
                        when {
                            errorMessage != null -> {
                                view.render(
                                    TracksState.ConnectionError(
                                        context.getString(R.string.connection_error),
                                        AppCompatResources.getDrawable(
                                            context,
                                            R.drawable.bad_connection_image
                                        )
                                    )
                                )
                            }

                            trackList.isEmpty() -> {
                                view.render(
                                    TracksState.NothingFound(
                                        context.getString(R.string.nothing_found),
                                        AppCompatResources.getDrawable(
                                            context,
                                            R.drawable.nothing_found_image
                                        )
                                    )
                                )
                            }

                            else -> view.render(TracksState.Content(trackList))
                        }
                    }
                }

            })
        }
    }

    fun showHideClearEditTextButton(text: String) {
        view.render(TracksState.ClearedEditText(text))
    }

    fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
    }
}