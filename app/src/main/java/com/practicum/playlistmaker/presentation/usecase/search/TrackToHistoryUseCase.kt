package com.practicum.playlistmaker.presentation.usecase.search

import com.practicum.playlistmaker.domain.models.Track

interface TrackToHistoryUseCase {
    fun execute(track: Track)
}