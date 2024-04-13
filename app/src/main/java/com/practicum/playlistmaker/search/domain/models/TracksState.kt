package com.practicum.playlistmaker.search.domain.models

import android.graphics.drawable.Drawable

interface TracksState {
    object Loading : TracksState
    object Empty : TracksState

    data class Content(val tracks: List<Track>) : TracksState
    data class ConnectionError(val errorMessage: String, val drawable: Drawable?) : TracksState
    data class NothingFound(val message: String, val drawable: Drawable?) : TracksState
    data class HistoryContent(val tracks: List<Track>) : TracksState
    data class ClearedEditText(val text: String) : TracksState
}