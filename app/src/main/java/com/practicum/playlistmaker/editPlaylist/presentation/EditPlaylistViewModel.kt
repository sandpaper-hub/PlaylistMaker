package com.practicum.playlistmaker.editPlaylist.presentation

import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylist.CreatePlaylistViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.model.CreatePlaylistState
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val createPlaylistInteractor: CreatePlaylistInteractor) :
    CreatePlaylistViewModel(createPlaylistInteractor) {

    fun initialize(playlist: Playlist) {
        renderState(CreatePlaylistState.Initialized(playlist))
    }

    override fun savePlaylist(uriCoverString: String?, fileCoverName: String, playlist: Playlist) {
        if (uriCoverString != null) {
            playlist.playlistCover =
                createPlaylistInteractor.saveCover(uriCoverString, fileCoverName)
        }
        viewModelScope.launch {
            createPlaylistInteractor.updatePlaylist(playlist)
        }
    }
}