package com.practicum.playlistmaker.search.presentation.viewModel

import android.graphics.drawable.Drawable
import com.practicum.playlistmaker.search.domain.models.Track

sealed interface TracksState {
    data object Loading : TracksState
    data object Empty : TracksState

    data class Content(val tracks: List<Track>) : TracksState
    data class ConnectionError(val errorMessage: String, val drawable: Drawable?) : TracksState
    data class NothingFound(val message: String, val drawable: Drawable?) : TracksState
    data class HistoryContent(val tracks: List<Track>) : TracksState
    data class ClearedEditText(val text: String) : TracksState
}