package com.practicum.playlistmaker.mediaLibrary.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.model.FavoriteTracksState

class FavoriteTracksViewModel(private val message: String) : ViewModel() {
    private val liveData = MutableLiveData<FavoriteTracksState>(FavoriteTracksState.EmptyMediaLibrary(message))
    fun observeState() = liveData
}