package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.MediaPlayerState

interface PlaybackControlUseCase {
    fun execute(playerState: MediaPlayerState): MediaPlayerState
}