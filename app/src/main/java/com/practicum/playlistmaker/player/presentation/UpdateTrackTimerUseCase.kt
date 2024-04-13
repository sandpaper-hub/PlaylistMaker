package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.MediaPlayerState

interface UpdateTrackTimerUseCase {
    fun execute(state: MediaPlayerState)
}