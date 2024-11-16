package com.practicum.playlistmaker.playlist.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.presentation.model.PlaylistState
import com.practicum.playlistmaker.util.reformatTimeMinutes
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> {
        return stateLiveData
    }

    fun initialize(playlistId: Int) {
        viewModelScope.launch {
            val listType = object : TypeToken<List<String>>() {}.type
            var totalTime = 0L
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                playlistInteractor.getAllTracks(Gson().fromJson(playlist.tracksId, listType))
                    .collect { tracks ->
                        for (track in tracks) {
                            totalTime += track.trackDuration!!
                        }
                        renderState(
                            PlaylistState.Initialized(
                                playlist,
                                totalTime.reformatTimeMinutes()
                            )
                        )
                    }
            }
        }
    }

    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }
}