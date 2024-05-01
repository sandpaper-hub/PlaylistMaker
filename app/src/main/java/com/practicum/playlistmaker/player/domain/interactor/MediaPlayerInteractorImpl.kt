package com.practicum.playlistmaker.player.domain.interactor

import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.wrapper.MediaPlayerWrapper
import com.practicum.playlistmaker.util.convertLongToTimeMillis

class MediaPlayerInteractorImpl(private val mediaPlayerWrapper: MediaPlayerWrapper) :
    MediaPlayerInteractor, MediaPlayerListener {

    companion object {
        private const val EMPTY_STRING = ""
    }

    override var isMediaPlayerComplete = false
    override var isMediaPlayerPrepared = false

    override fun preparePlayer(trackPreviewUrl: String?) {
        mediaPlayerWrapper.preparePlayer(trackPreviewUrl, this)
    }

    override fun isPrepared() {
        isMediaPlayerPrepared = true
    }

    override fun isComplete() {
        isMediaPlayerComplete = true
    }

    override fun startPlayer() {
        mediaPlayerWrapper.playerStart()
        isMediaPlayerComplete = false
    }

    override fun pausePlayer() {
        mediaPlayerWrapper.playerPause()
    }

    override fun releasePlayer() {
        mediaPlayerWrapper.playerRelease()
    }

    override fun getTrackPosition(): String {
        if (isMediaPlayerComplete) {
            return EMPTY_STRING
        }
        return mediaPlayerWrapper.getTrackPosition().toLong().convertLongToTimeMillis()
    }
}