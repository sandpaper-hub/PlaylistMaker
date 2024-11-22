package com.practicum.playlistmaker.playlist.presentation

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.presentation.model.PlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.reformatTimeMinutes
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val resources: Resources
) : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> {
        return stateLiveData
    }

    private lateinit var tracksList: List<Track>
    private lateinit var currentPlaylist: Playlist

    fun initialize(playlistId: Int) {
        viewModelScope.launch {
            val listType = object : TypeToken<List<String>>() {}.type
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                currentPlaylist = playlist
                playlistInteractor.getAllTracks(Gson().fromJson(playlist.tracksId, listType))
                    .collect { tracks ->
                        tracksList = tracks
                        renderState(
                            PlaylistState.Initialized(
                                playlist,
                                calculateTotalTime(), tracks
                            )
                        )
                    }
            }
        }
    }

    fun deleteTrack(trackId: String) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(trackId).collect { (playlist, tracks) ->
                tracksList = tracks
                currentPlaylist = playlist
                renderState(PlaylistState.Updated(tracks, playlist, calculateTotalTime()))
            }
        }
    }

    fun shareIntent() {
        renderState(PlaylistState.ShareIntent(tracksList, currentPlaylist))
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(currentPlaylist)
            renderState(PlaylistState.PlaylistDeleted)
        }
    }

    fun showDeleteTrackDialog(trackId: String) {
        renderState(
            PlaylistState.DialogTrackDelete(
                resources.getString(R.string.deleteTrack),
                trackId
            )
        )
    }

    fun showDeletePlaylistDialog() {
        renderState(
            PlaylistState.DialogPlaylistDelete(
                resources.getString(R.string.deletePlaylist),
                currentPlaylist.id
            )
        )
    }

    private fun calculateTotalTime(): String {
        var totalTime = 0L
        for (track in tracksList) {
            totalTime += track.trackDuration!!
        }
        return totalTime.reformatTimeMinutes()
    }

    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }
}