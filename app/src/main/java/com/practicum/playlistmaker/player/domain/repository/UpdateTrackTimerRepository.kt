package com.practicum.playlistmaker.player.domain.repository

interface UpdateTrackTimerRepository {
    fun startUpdatingTrackPosition()

    fun stopUpdatingTrackPosition()
}