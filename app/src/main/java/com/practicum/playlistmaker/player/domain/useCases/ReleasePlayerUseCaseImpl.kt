package com.practicum.playlistmaker.player.domain.useCases

import com.practicum.playlistmaker.player.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.player.presentation.ReleasePlayerUseCase

class ReleasePlayerUseCaseImpl(private val mediaPlayerHandler: MediaPlayerHandler):
    ReleasePlayerUseCase {
    override fun execute() {
        mediaPlayerHandler.releasePlayer()
    }
}