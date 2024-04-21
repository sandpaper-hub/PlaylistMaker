package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.wrapper.MediaPlayerWrapper

class MediaPlayerWrapperImpl : MediaPlayerWrapper {
    private val mediaPlayer = MediaPlayer()
    override fun preparePlayer(trackPreviewUrl: String?, mediaPlayerListener: MediaPlayerListener) {
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerListener.isPrepared()
        }

        mediaPlayer.setOnCompletionListener {
            mediaPlayerListener.isComplete()
        }
    }

    override fun playerStart() {
        mediaPlayer.start()
    }

    override fun playerPause() {
        mediaPlayer.pause()
    }

    override fun playerRelease() {
        mediaPlayer.release()
    }

    override fun getTrackPosition(): Int {
        return mediaPlayer.currentPosition
    }
}