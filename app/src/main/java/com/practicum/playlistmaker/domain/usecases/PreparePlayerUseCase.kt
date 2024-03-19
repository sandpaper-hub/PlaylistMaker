package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PreparePlayerUseCase(private val playerRepository: PlayerRepository) {
    companion object {
        private const val STATE_PREPARED = 1
    }
    fun execute(): Int {
        playerRepository.preparePlayer()
        return STATE_PREPARED
    }
}