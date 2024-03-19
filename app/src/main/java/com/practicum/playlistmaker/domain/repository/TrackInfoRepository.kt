package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.Track

interface TrackInfoRepository {
    fun getTrackInfo() : Track
}