package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.presentation.usecase.PlaybackControlUseCase

class PlaybackControlUseCaseImpl(private val mediaPlayerHandler: MediaPlayerHandler) :
    PlaybackControlUseCase {
    override fun execute(playerState: MediaPlayerState): MediaPlayerState {
        return when (playerState) {
            MediaPlayerState.STATE_PLAYING -> {
                mediaPlayerHandler.pausePlayer()
                MediaPlayerState.STATE_PAUSED
            }

            MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                mediaPlayerHandler.startPlayer()
                MediaPlayerState.STATE_PLAYING
            }

            else -> MediaPlayerState.STATE_DEFAULT
        }
    }
}