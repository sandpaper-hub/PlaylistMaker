package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.model.PlaylistsState

class PlaylistsViewModel(val message: String) : ViewModel() {
    private val liveData = MutableLiveData<PlaylistsState>(PlaylistsState.Empty(message))
    fun observeState() = liveData
}