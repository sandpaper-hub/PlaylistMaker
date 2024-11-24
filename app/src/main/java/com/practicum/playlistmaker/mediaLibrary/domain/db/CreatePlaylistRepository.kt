package com.practicum.playlistmaker.mediaLibrary.domain.db

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

interface CreatePlaylistRepository {
    suspend fun addNewPlaylist(playlist: Playlist)
    fun saveCover(uriString: String, fileName: String): String
    suspend fun updatePlaylist(playlist: Playlist)
}