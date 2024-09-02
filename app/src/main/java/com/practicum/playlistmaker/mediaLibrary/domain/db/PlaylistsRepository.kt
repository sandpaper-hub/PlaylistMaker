package com.practicum.playlistmaker.mediaLibrary.domain.db

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow


interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
}