package com.practicum.playlistmaker.player.domain.repository

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface MediaPlayerRepository {
    suspend fun addTrackToFavorite(track: Track)
    suspend fun removeTrackFromFavorite(track: Track)
    fun getFavoriteTracksId():Flow<List<String>>
}