package com.practicum.playlistmaker.playlist.presentation.model

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed interface PlaylistState {
    data class Initialized(val playlist: Playlist, val totalTime: String, val tracks: List<Track>) :
        PlaylistState

    data class Updated(val tracks: List<Track>, val playlist: Playlist, val totalTime: String) :
        PlaylistState

    data class ShareIntent(val tracks: List<Track>, val playlist: Playlist) : PlaylistState
    data class DialogPlaylistDelete(val message: String, val objectId: Int) : PlaylistState
    data class DialogTrackDelete(val message: String, val objectId: String): PlaylistState
    data object PlaylistDeleted: PlaylistState
}