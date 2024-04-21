package com.practicum.playlistmaker.player.domain.interactor

import com.practicum.playlistmaker.player.domain.model.MediaPlayerState

interface MediaPlayerInteractor {
    var isMediaPlayerComplete: Boolean
    var isMediaPlayerPrepared: Boolean
    fun preparePlayer(trackPreviewUrl: String?): MediaPlayerState
    fun playbackControl(playerState: MediaPlayerState): MediaPlayerState

    fun releasePlayer()

    fun getTrackPosition(): Int
}