package com.practicum.playlistmaker.presentation.usecase

import com.practicum.playlistmaker.MediaPlayerState

interface PlaybackControlUseCase {
    fun execute(playerState: MediaPlayerState): MediaPlayerState
}