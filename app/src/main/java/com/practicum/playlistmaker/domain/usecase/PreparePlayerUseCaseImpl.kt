package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.presentation.usecase.PreparePlayerUseCase

class PreparePlayerUseCaseImpl(private val mediaPlayerHandler: MediaPlayerHandler):
    PreparePlayerUseCase {
    override fun execute(): MediaPlayerState {
        mediaPlayerHandler.preparePlayer()
        return MediaPlayerState.STATE_PREPARED
    }
}