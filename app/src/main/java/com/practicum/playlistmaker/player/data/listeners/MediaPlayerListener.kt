package com.practicum.playlistmaker.player.data.listeners

interface MediaPlayerListener {
    fun onTrackPositionChanged(position: String)
    fun onPreparedPlayer()
    fun onTrackComplete()
    fun onPlayerStart()
    fun onPlayerPaused()
}