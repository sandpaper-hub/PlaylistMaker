package com.practicum.playlistmaker.presentation.usecase

import com.practicum.playlistmaker.MediaPlayerState

interface UpdateTrackTimerUseCase {
    fun execute(state: MediaPlayerState)
}