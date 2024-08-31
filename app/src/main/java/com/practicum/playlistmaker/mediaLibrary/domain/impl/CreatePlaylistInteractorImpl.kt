package com.practicum.playlistmaker.mediaLibrary.domain.impl

import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistRepository
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class CreatePlaylistInteractorImpl(
    private val createPlaylistRepository: CreatePlaylistRepository,
) : CreatePlaylistInteractor {
    override suspend fun addNewPlaylist(playlist: Playlist) {
        createPlaylistRepository.addNewPlaylist(playlist)
    }

    override fun saveCover(uriString: String, fileName: String) {
        createPlaylistRepository.saveCover(uriString, fileName)
    }
}