package com.practicum.playlistmaker.presentation.usecase

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.repository.PlayerRepository
import com.practicum.playlistmaker.domain.usecase.PlaybackControlUseCase

class PlaybackControlUseCaseImpl(private val playerRepository: PlayerRepository) :
    PlaybackControlUseCase {
    override fun execute(playerState: MediaPlayerState): MediaPlayerState {
        return when (playerState) {
            MediaPlayerState.STATE_PLAYING -> {
                playerRepository.pausePlayer()
                MediaPlayerState.STATE_PAUSED
            }

            MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                playerRepository.startPlayer()
                MediaPlayerState.STATE_PLAYING
            }

            else -> MediaPlayerState.STATE_DEFAULT
        }
    }
}