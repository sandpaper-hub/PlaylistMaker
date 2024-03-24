package com.practicum.playlistmaker.presentation.usecase.player

import com.practicum.playlistmaker.MediaPlayerState

interface PlaybackControlUseCase {
    fun execute(playerState: MediaPlayerState): MediaPlayerState
}