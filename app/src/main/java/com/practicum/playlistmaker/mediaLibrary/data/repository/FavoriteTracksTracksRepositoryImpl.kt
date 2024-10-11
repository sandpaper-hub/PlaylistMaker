package com.practicum.playlistmaker.mediaLibrary.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksTracksRepositoryImpl(
    private val trackDbConverter: TrackDbConverter,
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getFavoriteTracks().map { entities ->
            entities.map { entity ->
                trackDbConverter.map(entity)
            }
        }
    }
}