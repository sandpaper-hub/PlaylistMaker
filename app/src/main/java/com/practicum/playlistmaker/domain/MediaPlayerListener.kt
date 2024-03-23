package com.practicum.playlistmaker.domain

interface MediaPlayerListener {
    fun onTrackPositionChanged(position: String)
    fun onPreparedPlayer()
    fun onTrackComplete()
    fun onPlayerStart()
    fun onPlayerPaused()
}