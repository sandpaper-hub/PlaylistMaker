package com.practicum.playlistmaker.player.domain.interactor

import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.presentation.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.model.MediaPlayerState

class MediaPlayerInteractorImpl : MediaPlayerInteractor {

    override var isMediaPlayerPrepared = false
    override var isMediaPlayerComplete = false

    private val mediaPlayerWrapper = Creator.provideMediaPlayerWrapper()
    override fun preparePlayer(trackPreviewUrl: String?): MediaPlayerState {
        mediaPlayerWrapper.preparePlayer(trackPreviewUrl)
        return if (mediaPlayerWrapper.isPrepared) {
            MediaPlayerState.STATE_PREPARED
        } else {
            MediaPlayerState.STATE_DEFAULT
        }
    }

    override fun playbackControl(playerState: MediaPlayerState): MediaPlayerState {
        return when (playerState) {
            MediaPlayerState.STATE_PLAYING -> {
                mediaPlayerWrapper.playerPause()
                MediaPlayerState.STATE_PAUSED
            }

            MediaPlayerState.STATE_PAUSED, MediaPlayerState.STATE_PREPARED -> {
                mediaPlayerWrapper.playerStart()
                isMediaPlayerComplete = false
                MediaPlayerState.STATE_PLAYING
            }

            else -> MediaPlayerState.STATE_PREPARED
        }
    }

    override fun releasePlayer() {
        mediaPlayerWrapper.playerRelease()
    }

    override fun getTrackPosition(): Int {
        if (mediaPlayerWrapper.isComplete) {
            isMediaPlayerComplete = true
            return -1
        }
        return mediaPlayerWrapper.getTrackPosition()
    }
}