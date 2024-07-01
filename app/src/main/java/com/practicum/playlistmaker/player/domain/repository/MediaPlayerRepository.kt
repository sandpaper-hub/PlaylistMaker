package com.practicum.playlistmaker.player.domain.repository

import com.practicum.playlistmaker.search.data.dto.TrackDto
import kotlinx.coroutines.flow.Flow

interface MediaPlayerRepository {
    suspend fun addTrackToFavorite(track: TrackDto)
    suspend fun removeTrackFromFavorite(track: TrackDto)
    fun getFavoriteTracksId():Flow<List<String>>
}