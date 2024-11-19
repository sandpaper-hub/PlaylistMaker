package com.practicum.playlistmaker.playlist.domain.db

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun getAllTracks(trackIds: List<String>): Flow<List<Track>>
    suspend fun getPlaylistById(playlistId: Int): Flow<Playlist>
    suspend fun deleteTrackFromPlaylist(trackId: String): Flow<Pair<Playlist, List<Track>>>
}