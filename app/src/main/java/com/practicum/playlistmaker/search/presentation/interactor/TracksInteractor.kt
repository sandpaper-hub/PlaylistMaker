package com.practicum.playlistmaker.search.presentation.interactor

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun addTrackToHistory(track: Track)

    fun getHistory(): ArrayList<Track>

    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}