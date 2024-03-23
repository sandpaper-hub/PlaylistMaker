package com.practicum.playlistmaker.presentation.usecase

import com.practicum.playlistmaker.domain.models.Track

interface GetTrackInfoUseCase {
    fun execute() : Track
}