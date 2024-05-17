package com.practicum.playlistmaker.mediaLibrary.presentation.model

sealed interface FavoriteTracksState {
    data class EmptyMediaLibrary(val message: String) : FavoriteTracksState
}