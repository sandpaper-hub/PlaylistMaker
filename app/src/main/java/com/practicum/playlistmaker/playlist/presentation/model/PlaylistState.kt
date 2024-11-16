package com.practicum.playlistmaker.playlist.presentation.model

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

sealed interface PlaylistState {
    data class Initialized(val playlist: Playlist, val totalTime: String) : PlaylistState
}