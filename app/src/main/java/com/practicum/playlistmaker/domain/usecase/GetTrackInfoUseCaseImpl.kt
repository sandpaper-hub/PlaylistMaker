package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackInfoRepository
import com.practicum.playlistmaker.presentation.usecase.GetTrackInfoUseCase

class GetTrackInfoUseCaseImpl(private val trackInfoRepository: TrackInfoRepository) :
    GetTrackInfoUseCase {
    override fun execute(): Track {
        return trackInfoRepository.getTrackInfo()
    }
}