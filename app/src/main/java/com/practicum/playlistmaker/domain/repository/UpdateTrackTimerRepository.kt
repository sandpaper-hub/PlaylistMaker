package com.practicum.playlistmaker.domain.repository

interface UpdateTrackTimerRepository {
    fun startUpdatingTrackPosition()

    fun stopUpdatingTrackPosition()
}