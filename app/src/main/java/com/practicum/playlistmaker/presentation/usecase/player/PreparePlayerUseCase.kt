package com.practicum.playlistmaker.presentation.usecase.player

import com.practicum.playlistmaker.MediaPlayerState

interface PreparePlayerUseCase {
    fun execute() : MediaPlayerState
}