package com.practicum.playlistmaker.mediaLibrary.presentation.model

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksState {
    data object EmptyMediaLibrary : FavoriteTracksState
    data class Content(val tracks: List<Track>) : FavoriteTracksState
}