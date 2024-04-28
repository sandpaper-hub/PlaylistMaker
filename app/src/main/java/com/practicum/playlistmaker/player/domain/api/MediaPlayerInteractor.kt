package com.practicum.playlistmaker.player.domain.api

interface MediaPlayerInteractor {
    var isMediaPlayerComplete: Boolean
    var isMediaPlayerPrepared: Boolean
    fun preparePlayer(trackPreviewUrl: String?)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()

    fun getTrackPosition(): String
}