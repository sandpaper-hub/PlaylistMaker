package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PlaybackControlUseCase(private val playerRepository: PlayerRepository) {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    fun execute(playerState: Int): Int {
        return when (playerState) {
            STATE_PLAYING -> {
                playerRepository.pausePlayer()
                STATE_PAUSED
            }

            STATE_PREPARED, STATE_PAUSED -> {
                playerRepository.startPlayer()
                STATE_PLAYING
            }

            else -> STATE_DEFAULT
        }
    }
}