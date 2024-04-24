package com.practicum.playlistmaker.search.presentation.viewModel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface TracksState {
    data object Loading : TracksState
    data object Empty : TracksState

    data class Content(val tracks: List<Track>) : TracksState
    data object ConnectionError : TracksState
    data object NothingFound : TracksState
    data class HistoryContent(val tracks: List<Track>) : TracksState
    data class ClearedEditText(val text: String) : TracksState
}