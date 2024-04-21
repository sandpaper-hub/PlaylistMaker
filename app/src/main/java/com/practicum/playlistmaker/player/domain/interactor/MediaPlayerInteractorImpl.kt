package com.practicum.playlistmaker.player.domain.interactor

import com.practicum.playlistmaker.creator.Creator

class MediaPlayerInteractorImpl : MediaPlayerInteractor, MediaPlayerListener {

    override var isMediaPlayerComplete = false
    override var isMediaPlayerPrepared = false

    private val mediaPlayerWrapper = Creator.provideMediaPlayerWrapper()
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
    }

    override fun pausePlayer() {
        mediaPlayerWrapper.playerPause()
    }

    override fun releasePlayer() {
        mediaPlayerWrapper.playerRelease()
    }

    override fun getTrackPosition(): Int {
        if (isMediaPlayerComplete) {
            isMediaPlayerComplete = true
            return -1
        }
        return mediaPlayerWrapper.getTrackPosition()
    }
}