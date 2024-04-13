package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.domain.models.TracksState

interface TracksView {
    fun render(state: TracksState)
}