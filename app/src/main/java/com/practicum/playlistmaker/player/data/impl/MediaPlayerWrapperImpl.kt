package com.practicum.playlistmaker.player.data.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.wrapper.MediaPlayerWrapper

class MediaPlayerWrapperImpl : MediaPlayerWrapper {
    private val mediaPlayer = MediaPlayer()
    override var isPrepared = false
    override var isComplete = false
    override fun preparePlayer(trackPreviewUrl: String?) {
        mediaPlayer.setDataSource(trackPreviewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            isPrepared = true
        }

        mediaPlayer.setOnCompletionListener {
            isComplete = true
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