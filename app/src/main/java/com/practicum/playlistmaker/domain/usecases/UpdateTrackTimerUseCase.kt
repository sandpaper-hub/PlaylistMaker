package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.repository.UpdateTrackTimerRepository

class UpdateTrackTimerUseCase(private val updateTrackTimerRepository: UpdateTrackTimerRepository) {
    companion object {
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
    fun execute(state: Int) {
        when (state) {
            STATE_PLAYING -> updateTrackTimerRepository.startUpdatingTrackPosition()
            STATE_PAUSED, STATE_PREPARED  -> updateTrackTimerRepository.stopUpdatingTrackPosition()
        }
    }
}