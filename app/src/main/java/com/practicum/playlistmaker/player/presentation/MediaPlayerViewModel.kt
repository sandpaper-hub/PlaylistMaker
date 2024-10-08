package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.presentation.model.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    companion object {
        private const val UPDATE_POSITION_DELAY = 150L
        private const val CHECK_PREPARE_PLAYER_DELAY = 50L
        private const val EMPTY_STRING = ""
    }

    private var isCreated = false
    private var isFavorite = false
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState>{
        return stateLiveData
    }

    private var timerJob: Job? = null
    private var prepareJob: Job? = null

    fun createPlayer(trackId: String?) {
        viewModelScope.launch {
            mediaPlayerInteractor.getFavoriteTracksId().collect { ids ->
                if (ids.contains(trackId)) {
                    isFavorite = true
                }
                renderState(PlayerState.Created(isFavorite))
            }
        }
    }

    fun preparePlayer(trackPreviewUrl: String?) {
        if (!isCreated) {
            mediaPlayerInteractor.preparePlayer(trackPreviewUrl)
            isCreated = true
        }
        prepareJob = viewModelScope.launch {
            while (!mediaPlayerInteractor.isMediaPlayerPrepared) {
                delay(CHECK_PREPARE_PLAYER_DELAY)
            }
            renderState(PlayerState.Prepared(mediaPlayerInteractor.getTrackPosition()))
        }
    }

    fun playbackControl() {
        when (stateLiveData.value) {
            is PlayerState.Prepared, PlayerState.Pause, PlayerState.Complete, PlayerState.Favorite(
                true
            ), PlayerState.Favorite(false) -> {
                mediaPlayerInteractor.startPlayer()
                renderState(PlayerState.Playing)
                timerJob?.cancel()
                timerJob = viewModelScope.launch {
                    while (!mediaPlayerInteractor.isMediaPlayerComplete) {
                        delay(UPDATE_POSITION_DELAY)
                        completeMediaPlayer()
                        updateTrackTimer()
                    }
                }
            }

            is PlayerState.Playing, PlayerState.ChangePosition(
                mediaPlayerInteractor.getTrackPosition()
            ) -> {
                pausePlayer()
            }

            else -> {
                renderState(PlayerState.Prepared(mediaPlayerInteractor.getTrackPosition()))
            }
        }
    }

    fun pausePlayer() {
        if (stateLiveData.value != PlayerState.Prepared(mediaPlayerInteractor.getTrackPosition())) {
            mediaPlayerInteractor.pausePlayer()
            renderState(PlayerState.Pause)
            timerJob?.cancel()
        }
    }

    fun completeMediaPlayer() {
        if (mediaPlayerInteractor.isMediaPlayerComplete) {
            timerJob?.cancel()
            renderState(PlayerState.Complete)
        }
    }

    private fun updateTrackTimer() {
        if (mediaPlayerInteractor.getTrackPosition() == EMPTY_STRING) {
            return
        }
        renderState(
            PlayerState.ChangePosition(
                mediaPlayerInteractor.getTrackPosition()
            )
        )
    }

    fun updateFavorite(track: Track) {
        viewModelScope.launch {
            if (isFavorite) {
                mediaPlayerInteractor.removeTrackFromFavorite(track)
                isFavorite = false
            } else {
                mediaPlayerInteractor.addTrackToFavorite(track)
                isFavorite = true
            }
            renderState(PlayerState.Favorite(isFavorite))
        }
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.releasePlayer()
    }
}