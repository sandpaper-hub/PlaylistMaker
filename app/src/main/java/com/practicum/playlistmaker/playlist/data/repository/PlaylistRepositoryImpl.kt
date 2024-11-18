package com.practicum.playlistmaker.playlist.data.repository

import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.playlist.domain.db.PlaylistRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.createPlaylistIdsArrayListFromJson
import com.practicum.playlistmaker.util.deleteId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class PlaylistRepositoryImpl(
    private val trackDbConverter: TrackDbConverter,
    private val playlistDbConverter: PlaylistDbConverter,
    private val appDatabase: AppDatabase
) : PlaylistRepository {

    private lateinit var currentPlaylist: Playlist

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
        val playlistFlow = appDatabase.playlistDao().getPlaylistById(playlistId).map { entity ->
            Playlist(
                entity.id,
                entity.playlistName,
                entity.playlistDescription,
                entity.playlistCover,
                entity.tracksId,
                entity.tracksCount
            )
        }

        playlistFlow.firstOrNull()?.let {
            currentPlaylist = it
        }
        return playlistFlow
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String): Flow<List<Track>> {
        currentPlaylist.tracksId = currentPlaylist.tracksId.deleteId(trackId)
        currentPlaylist.tracksCount -= 1
        appDatabase.playlistDao().updateTracksId(playlistDbConverter.map(currentPlaylist))

        appDatabase.playlistDao().getPlaylists().onEach { playlists ->
            if (playlists.none { it.tracksId?.contains(trackId) == true }) {
                appDatabase.inPlaylistsDao().removeTrackEntity(trackId.toInt())
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))

        return getAllTracks(currentPlaylist.tracksId!!.createPlaylistIdsArrayListFromJson())
    }
}