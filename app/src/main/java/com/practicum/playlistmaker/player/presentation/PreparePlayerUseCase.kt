package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.MediaPlayerState

interface PreparePlayerUseCase {
    fun execute() : MediaPlayerState
}