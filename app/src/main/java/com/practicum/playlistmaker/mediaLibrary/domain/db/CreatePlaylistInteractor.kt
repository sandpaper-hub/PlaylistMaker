package com.practicum.playlistmaker.mediaLibrary.domain.db

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface CreatePlaylistInteractor {
    suspend fun addNewPlaylist(playlist: Playlist)
    fun saveCover(uriString: String, fileName: String)
}