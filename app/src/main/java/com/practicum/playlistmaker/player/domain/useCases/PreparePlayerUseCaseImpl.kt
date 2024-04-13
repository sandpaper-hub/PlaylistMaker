package com.practicum.playlistmaker.player.domain.useCases

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.player.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.player.presentation.PreparePlayerUseCase

class PreparePlayerUseCaseImpl(private val mediaPlayerHandler: MediaPlayerHandler):
    PreparePlayerUseCase {
    override fun execute(): MediaPlayerState {
        mediaPlayerHandler.preparePlayer()
        return MediaPlayerState.STATE_PREPARED
    }
}