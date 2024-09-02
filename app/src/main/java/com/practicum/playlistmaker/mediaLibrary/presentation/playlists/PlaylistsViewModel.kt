package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.model.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {
    private val liveData = MutableLiveData<PlaylistsState>()
    fun observeState() = liveData

    fun fillData() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists.asReversed()))
        }
    }

    private fun renderState(state: PlaylistsState) {
        liveData.postValue(state)
    }
}