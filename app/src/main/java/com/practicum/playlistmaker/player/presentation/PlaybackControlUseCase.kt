package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.MediaPlayerState

interface PlaybackControlUseCase {
    fun execute(playerState: MediaPlayerState): MediaPlayerState
}