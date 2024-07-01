package com.practicum.playlistmaker.mediaLibrary.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    fun getFavoriteTracks(): Flow<List<Track>>
}