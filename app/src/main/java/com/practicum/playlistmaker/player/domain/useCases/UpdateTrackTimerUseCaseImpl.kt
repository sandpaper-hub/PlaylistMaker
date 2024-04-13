package com.practicum.playlistmaker.player.domain.useCases

import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.player.domain.repository.UpdateTrackTimerRepository
import com.practicum.playlistmaker.player.presentation.UpdateTrackTimerUseCase

class UpdateTrackTimerUseCaseImpl(private val updateTrackTimerRepository: UpdateTrackTimerRepository):
    UpdateTrackTimerUseCase {
    override fun execute(state: MediaPlayerState) {
        when (state) {
            MediaPlayerState.STATE_PLAYING -> updateTrackTimerRepository.startUpdatingTrackPosition()
            MediaPlayerState.STATE_PAUSED, MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_DEFAULT -> updateTrackTimerRepository.stopUpdatingTrackPosition()
        }
    }
}