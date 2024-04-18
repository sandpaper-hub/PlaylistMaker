package com.practicum.playlistmaker.player.domain.wrapper

interface MediaPlayerWrapper {
    var isComplete: Boolean
    fun preparePlayer(trackPreviewUrl: String?)
    fun playerStart()
    fun playerPause()
    fun playerRelease()
    fun getTrackPosition(): Int
}