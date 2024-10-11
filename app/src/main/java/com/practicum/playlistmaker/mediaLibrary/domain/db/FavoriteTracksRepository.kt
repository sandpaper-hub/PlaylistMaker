package com.practicum.playlistmaker.mediaLibrary.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
   suspend fun getFavoriteTracks(): Flow<List<Track>>
}