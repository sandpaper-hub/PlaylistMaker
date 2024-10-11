package com.practicum.playlistmaker.mediaLibrary.presentation.model

sealed interface CreatePlaylistState {
    data class EnableCreateButton(val isEnable: Boolean): CreatePlaylistState
    data object Dialog: CreatePlaylistState
}