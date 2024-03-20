package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PreparePlayerUseCase(private val playerRepository: PlayerRepository) {
    fun execute(): MediaPlayerState {
        playerRepository.preparePlayer()
        return MediaPlayerState.STATE_PREPARED
    }
}