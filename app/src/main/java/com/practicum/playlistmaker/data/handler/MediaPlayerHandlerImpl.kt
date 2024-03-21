package com.practicum.playlistmaker.data.handler

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler

class MediaPlayerHandlerImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackPreviewUrl: String?
) : MediaPlayerHandler {
    override fun preparePlayer() {
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }
}