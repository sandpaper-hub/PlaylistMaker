package com.practicum.playlistmaker.domain.handler

interface MediaPlayerHandler {
    fun preparePlayer()

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()
}