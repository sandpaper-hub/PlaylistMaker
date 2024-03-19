package com.practicum.playlistmaker.domain.repository

interface PlayerRepository {
    fun preparePlayer()

    fun startPlayer()

    fun pausePlayer()
}