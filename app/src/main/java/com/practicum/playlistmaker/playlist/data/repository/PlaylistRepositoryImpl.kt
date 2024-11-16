package com.practicum.playlistmaker.playlist.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.playlist.domain.db.PlaylistRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val trackDbConverter: TrackDbConverter,
    private val appDatabase: AppDatabase
) : PlaylistRepository {
    override suspend fun getAllTracks(trackIds: List<String>): Flow<List<Track>> {
        return appDatabase.inPlaylistsDao().getAllTracks().map { entities ->
            entities.map { entity ->
                trackDbConverter.map(entity)
            }.filter { track ->
                track.trackId in trackIds
            }
        }
    }

    override suspend fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return appDatabase.playlistDao().getPlaylistById(playlistId).map { entity ->
            Playlist(
                entity.id,
                entity.playlistName,
                entity.playlistDescription,
                entity.playlistCover,
                entity.tracksId,
                entity.tracksCount
            )
        }
    }
}