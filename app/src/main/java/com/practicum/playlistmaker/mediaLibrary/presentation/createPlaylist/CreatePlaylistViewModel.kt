package com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.model.CreatePlaylistState
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(private val createPlaylistInteractor: CreatePlaylistInteractor) :
    ViewModel() {

    companion object {
        private const val CREATE_BUTTON_ENABLED = true
        private const val CREATE_BUTTON_DISABLED = false
    }

    val stateLiveData = MutableLiveData<CreatePlaylistState>()

    fun observeState(): LiveData<CreatePlaylistState> {
        return stateLiveData
    }

    fun checkCreateButton(nameIsEmpty: Boolean) {
        if (nameIsEmpty) {
            renderState(CreatePlaylistState.EnableCreateButton(CREATE_BUTTON_ENABLED))
        } else {
            renderState(CreatePlaylistState.EnableCreateButton(CREATE_BUTTON_DISABLED))
        }
    }

    fun showDialog() {
        renderState(CreatePlaylistState.Dialog)
    }

   fun renderState(state: CreatePlaylistState) {
        stateLiveData.postValue(state)
    }

    fun savePlaylist(uriCoverString: String?, fileCoverName: String, playlist: Playlist) {
        if (uriCoverString != null) {
            playlist.playlistCover =
                createPlaylistInteractor.saveCover(uriCoverString, fileCoverName)
        }
        viewModelScope.launch {
            createPlaylistInteractor.addNewPlaylist(playlist)
        }
    }
}