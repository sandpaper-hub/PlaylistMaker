package com.practicum.playlistmaker.mediaLibrary.presentation.model

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

interface CreatePlaylistState {
    data class EnableCreateButton(val isEnable: Boolean): CreatePlaylistState
    data object Dialog: CreatePlaylistState
    data class Initialized(val playlist: Playlist) : CreatePlaylistState
}