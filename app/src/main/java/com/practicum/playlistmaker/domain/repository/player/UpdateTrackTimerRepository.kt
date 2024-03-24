package com.practicum.playlistmaker.domain.repository.player

interface UpdateTrackTimerRepository {
    fun startUpdatingTrackPosition()

    fun stopUpdatingTrackPosition()
}