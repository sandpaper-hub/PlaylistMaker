package com.practicum.playlistmaker.presentation.usecase

import com.practicum.playlistmaker.MediaPlayerState

interface PreparePlayerUseCase {
    fun execute() : MediaPlayerState
}