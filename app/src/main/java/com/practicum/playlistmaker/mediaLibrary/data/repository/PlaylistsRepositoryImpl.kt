package com.practicum.playlistmaker.mediaLibrary.data.repository

import com.google.gson.Gson
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.createPlaylistIdsArrayListFromJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val appDatabase: AppDatabase
) : PlaylistsRepository {

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists().map { entities ->
            entities.map { entity ->
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

    override suspend fun updatePlaylistTracksId(playlist: Playlist, track: Track) {
        val playlistEntity = playlistDbConverter.map(playlist)
        val idsArrayList = playlistEntity.tracksId?.createPlaylistIdsArrayListFromJson()
        val trackInPlaylistsEntity = trackDbConverter.mapToTrackInPlaylistsEntity(track)
        idsArrayList?.add(trackInPlaylistsEntity.id)
        playlistEntity.tracksId = Gson().toJson(idsArrayList)
        playlistEntity.tracksCount++
        appDatabase.inPlaylistsDao().addTrack(trackInPlaylistsEntity)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }
}