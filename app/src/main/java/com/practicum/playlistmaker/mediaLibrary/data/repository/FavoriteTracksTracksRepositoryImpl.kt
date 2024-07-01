package com.practicum.playlistmaker.mediaLibrary.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.domain.api.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksTracksRepositoryImpl(
    private val trackDbConverter: TrackDbConverter,
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavoriteTracks()
        emit(trackDbConverter.convertFromTracksEntity(tracks))
    }
}