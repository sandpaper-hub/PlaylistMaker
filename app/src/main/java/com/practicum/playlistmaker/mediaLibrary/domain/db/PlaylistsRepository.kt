package com.practicum.playlistmaker.mediaLibrary.domain.db

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface PlaylistsRepository {
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylistTracksId(playlist: Playlist, track: Track)
}