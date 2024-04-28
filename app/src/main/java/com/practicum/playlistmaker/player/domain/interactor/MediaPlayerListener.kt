package com.practicum.playlistmaker.player.domain.interactor

interface MediaPlayerListener {
    fun isPrepared()
    fun isComplete()
}