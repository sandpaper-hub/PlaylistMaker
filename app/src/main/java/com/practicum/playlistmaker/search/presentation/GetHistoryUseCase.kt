package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.domain.models.Track

interface GetHistoryUseCase {
    fun execute(): ArrayList<Track>
}