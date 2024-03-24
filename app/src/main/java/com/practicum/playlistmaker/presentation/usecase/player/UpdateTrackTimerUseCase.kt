package com.practicum.playlistmaker.presentation.usecase.player

import com.practicum.playlistmaker.MediaPlayerState

interface UpdateTrackTimerUseCase {
    fun execute(state: MediaPlayerState)
}