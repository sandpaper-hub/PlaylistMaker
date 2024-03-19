package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackPreviewUrl: String?
) : PlayerRepository {
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