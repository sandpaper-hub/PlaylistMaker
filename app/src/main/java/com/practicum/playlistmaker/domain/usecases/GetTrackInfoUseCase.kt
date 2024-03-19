package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackInfoRepository

class GetTrackInfoUseCase(private val trackInfoRepository: TrackInfoRepository) {
    fun execute(): Track {
        return trackInfoRepository.getTrackInfo()
    }
}