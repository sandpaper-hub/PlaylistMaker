package com.practicum.playlistmaker.domain

interface TrackPositionListener {
    fun onTrackPositionChanged(position: Int)
}