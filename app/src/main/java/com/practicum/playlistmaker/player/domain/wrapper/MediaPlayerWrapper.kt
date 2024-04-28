package com.practicum.playlistmaker.player.domain.wrapper

import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerListener

interface MediaPlayerWrapper {
    fun preparePlayer(trackPreviewUrl: String?, mediaPlayerListener: MediaPlayerListener)
    fun playerStart()
    fun playerPause()
    fun playerRelease()
    fun getTrackPosition(): Int
}