package com.practicum.playlistmaker.player.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlayerRepositoryImpl(
    private val trackDbConverter: TrackDbConverter,
    private val appDatabase: AppDatabase
) : MediaPlayerRepository {
    override suspend fun addTrackToFavorite(track: Track) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)

    }

    override suspend fun removeTrackFromFavorite(track: Track) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override suspend fun getFavoriteTracksId(): Flow<List<String>>{
        return appDatabase.trackDao().getTracksId()
    }
}