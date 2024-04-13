package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.domain.models.Track

interface TrackToHistoryUseCase {
    fun execute(track: Track)
}