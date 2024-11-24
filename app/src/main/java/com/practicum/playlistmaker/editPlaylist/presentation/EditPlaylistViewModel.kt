package com.practicum.playlistmaker.editPlaylist.presentation

import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylist.CreatePlaylistViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.model.CreatePlaylistState

class EditPlaylistViewModel(private val createPlaylistInteractor: CreatePlaylistInteractor) :
    CreatePlaylistViewModel(createPlaylistInteractor) {

    fun initialize(playlist: Playlist) {
        renderState(CreatePlaylistState.Initialized(playlist))
    }
}