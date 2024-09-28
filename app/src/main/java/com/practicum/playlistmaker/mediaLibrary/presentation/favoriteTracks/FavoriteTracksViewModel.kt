package com.practicum.playlistmaker.mediaLibrary.presentation.favoriteTracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.presentation.model.FavoriteTracksState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeState():LiveData<FavoriteTracksState>{
        return stateLiveData
    }

    fun fillData() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteTracksState.EmptyMediaLibrary)
        } else {
            renderState(FavoriteTracksState.Content(tracks.asReversed()))
        }
    }

    private fun renderState(state: FavoriteTracksState) {
        stateLiveData.postValue(state)
    }
}