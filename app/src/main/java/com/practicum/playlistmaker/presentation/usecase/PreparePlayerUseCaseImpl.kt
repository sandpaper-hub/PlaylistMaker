package com.practicum.playlistmaker.presentation.usecase

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.repository.PlayerRepository
import com.practicum.playlistmaker.domain.usecase.PreparePlayerUseCase

class PreparePlayerUseCaseImpl(private val playerRepository: PlayerRepository):
    PreparePlayerUseCase {
    override fun execute(): MediaPlayerState {
        playerRepository.preparePlayer()
        return MediaPlayerState.STATE_PREPARED
    }
}