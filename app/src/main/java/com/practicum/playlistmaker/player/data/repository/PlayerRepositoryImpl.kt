package com.practicum.playlistmaker.player.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.dto.TrackDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlayerRepositoryImpl(
    private val trackDbConverter: TrackDbConverter,
    private val appDatabase: AppDatabase
) : MediaPlayerRepository {
    override suspend fun addTrackToFavorite(track: TrackDto) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)

    }

    override suspend fun removeTrackFromFavorite(track: TrackDto) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getFavoriteTracksId(): Flow<List<String>> = flow {
       emit(appDatabase.trackDao().getTracksId())
    }
}