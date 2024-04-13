package com.practicum.playlistmaker.player.domain.handler

interface MediaPlayerHandler {
    fun preparePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()
}