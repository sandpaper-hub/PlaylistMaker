package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.presentation.usecase.ReleasePlayerUseCase

class ReleasePlayerUseCaseImpl(private val mediaPlayerHandler: MediaPlayerHandler): ReleasePlayerUseCase {
    override fun execute() {
        mediaPlayerHandler.releasePlayer()
    }
}