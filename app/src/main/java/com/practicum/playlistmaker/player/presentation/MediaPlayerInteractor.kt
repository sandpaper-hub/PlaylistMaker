package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.player.domain.model.MediaPlayerState

interface MediaPlayerInteractor {
    var isMediaPlayerComplete:Boolean
    fun preparePlayer(trackPreviewUrl: String?)
    fun playbackControl(playerState: MediaPlayerState): MediaPlayerState

    fun releasePlayer()

    fun getTrackPosition(): Int
}