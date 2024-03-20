package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.MediaPlayerState

interface PreparePlayerUseCase {
    fun execute() : MediaPlayerState
}