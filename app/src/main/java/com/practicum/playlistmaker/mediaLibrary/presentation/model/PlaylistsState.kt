package com.practicum.playlistmaker.mediaLibrary.presentation.model

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

sealed interface PlaylistsState {
    data class Content(val playlists: List<Playlist>): PlaylistsState
    data object Empty : PlaylistsState
    data class AddingResult(val playlist: Playlist, val result: Boolean) : PlaylistsState
}