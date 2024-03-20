package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PlaybackControlUseCase(private val playerRepository: PlayerRepository) {
    fun execute(playerState: MediaPlayerState): MediaPlayerState {
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