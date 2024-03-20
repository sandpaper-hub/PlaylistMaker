package com.practicum.playlistmaker.domain.usecase

import com.practicum.playlistmaker.domain.models.Track

interface GetTrackInfoUseCase {
    fun execute() : Track
}