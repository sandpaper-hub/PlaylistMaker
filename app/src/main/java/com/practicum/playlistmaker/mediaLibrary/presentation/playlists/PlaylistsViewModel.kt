package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.model.PlaylistsState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {
    private val liveData = MutableLiveData<PlaylistsState>()
    fun observeState() = liveData

    fun fillData() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
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

    fun updatePlaylist(playlist: Playlist, track: Track) {
        val isAddedToDataBase = true
        if(track.trackId.toString() in playlist.tracksId.orEmpty()){
            renderState(PlaylistsState.AddingResult(playlist, !isAddedToDataBase))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                playlistsInteractor.updatePlaylistIds(playlist, track)
                renderState(PlaylistsState.AddingResult(playlist, isAddedToDataBase))
            }
        }
    }

    private fun renderState(state: PlaylistsState) {
        liveData.postValue(state)
    }
}