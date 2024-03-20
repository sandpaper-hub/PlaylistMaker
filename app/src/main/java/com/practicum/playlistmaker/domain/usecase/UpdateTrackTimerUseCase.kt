package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.MediaPlayerState

interface UpdateTrackTimerUseCase {
    fun execute(state: MediaPlayerState)
}