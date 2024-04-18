package com.practicum.playlistmaker.player.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.convertLongToTimeMillis
import com.practicum.playlistmaker.player.domain.model.MediaPlayerState
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.ui.model.PlayerState

class MediaPlayerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val UPDATE_POSITION_DELAY = 250L
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { MediaPlayerViewModel(this[APPLICATION_KEY] as Application) }
        }
    }

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData


    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()

    private val handler = Handler(Looper.getMainLooper())

    private val timerUpdater = object : Runnable {
        override fun run() {
            completeMediaPlayer()
            if (!mediaPlayerInteractor.isMediaPlayerComplete) {
                updateTrackTimer()
                handler.postDelayed(this, UPDATE_POSITION_DELAY)
            }
        }
    }

    fun playbackControl(playerState: MediaPlayerState): MediaPlayerState {
        val mediaPlayerState = mediaPlayerInteractor.playbackControl(playerState)
        return if (mediaPlayerState == MediaPlayerState.STATE_PLAYING) {
            renderState(PlayerState.Playing)
            timerUpdater.let { handler.post(it) }
            mediaPlayerState
        } else {
            renderState(PlayerState.Pause)
            timerUpdater.let { handler.removeCallbacks(it) }
            mediaPlayerState
        }
    }

    fun preparePlayer(trackPreviewUrl: String?): MediaPlayerState {
        mediaPlayerInteractor.preparePlayer(trackPreviewUrl)
        renderState(PlayerState.Prepared)
        return if (mediaPlayerInteractor.isMediaPlayerPrepared) {
            MediaPlayerState.STATE_PREPARED
        } else MediaPlayerState.STATE_DEFAULT
    }

    fun releaseMediaPlayer() {
        mediaPlayerInteractor.releasePlayer()
    }

    fun completeMediaPlayer() {
        if (mediaPlayerInteractor.isMediaPlayerComplete) {
            timerUpdater.let { handler.removeCallbacks(it) }
            renderState(PlayerState.Complete)
        }
    }

    private fun updateTrackTimer() {
        if (mediaPlayerInteractor.getTrackPosition() == -1) {
            return
        }
        val trackPosition = mediaPlayerInteractor.getTrackPosition().toLong().convertLongToTimeMillis()
        renderState(PlayerState.ChangePosition(trackPosition))
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        timerUpdater.let { handler.removeCallbacks(it) }
    }

}