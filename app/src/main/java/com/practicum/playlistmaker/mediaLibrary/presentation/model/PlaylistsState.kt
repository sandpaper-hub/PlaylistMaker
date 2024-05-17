package com.practicum.playlistmaker.mediaLibrary.presentation.model

sealed interface PlaylistsState {
    data class Empty(val message: String): PlaylistsState
}