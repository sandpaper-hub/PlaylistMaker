package com.practicum.playlistmaker.data.handler

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler

class MediaPlayerHandlerImpl(
    private val playerListener: MediaPlayerListener,
    private val trackPreviewUrl: String?
) : MediaPlayerHandler {

    val mediaPlayer = MediaPlayer()

    override fun preparePlayer() {
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerListener.onPreparedPlayer()
        }
        mediaPlayer.setOnCompletionListener {
            playerListener.onTrackComplete()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerListener.onPlayerStart()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerListener.onPlayerPaused()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }
}