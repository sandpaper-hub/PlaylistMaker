package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.domain.repository.UpdateTrackTimerRepository
import com.practicum.playlistmaker.presentation.usecase.UpdateTrackTimerUseCase

class UpdateTrackTimerUseCaseImpl(private val updateTrackTimerRepository: UpdateTrackTimerRepository):
    UpdateTrackTimerUseCase {
    override fun execute(state: MediaPlayerState) {
        when (state) {
            MediaPlayerState.STATE_PLAYING -> updateTrackTimerRepository.startUpdatingTrackPosition()
            MediaPlayerState.STATE_PAUSED, MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_DEFAULT -> updateTrackTimerRepository.stopUpdatingTrackPosition()
        }
    }
}