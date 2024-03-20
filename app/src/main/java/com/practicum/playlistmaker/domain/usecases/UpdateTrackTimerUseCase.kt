package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.repository.UpdateTrackTimerRepository

class UpdateTrackTimerUseCase(private val updateTrackTimerRepository: UpdateTrackTimerRepository) {
    fun execute(state: MediaPlayerState) {
        when (state) {
            MediaPlayerState.STATE_PLAYING -> updateTrackTimerRepository.startUpdatingTrackPosition()
            MediaPlayerState.STATE_PAUSED, MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_DEFAULT -> updateTrackTimerRepository.stopUpdatingTrackPosition()
        }
    }
}