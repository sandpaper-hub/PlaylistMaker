package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface MediaPlayerInteractor {
    var isMediaPlayerComplete: Boolean
    var isMediaPlayerPrepared: Boolean
    fun preparePlayer(trackPreviewUrl: String?)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getTrackPosition(): String
    suspend fun addTrackToFavorite(track: Track)
    suspend fun removeTrackFromFavorite(track: Track)
    fun getFavoriteTracksId():Flow<List<String>>
}