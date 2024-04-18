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


    private val mediaPlayerHandler = Creator.provideMediaPlayerInteractor()

    private val handler = Handler(Looper.getMainLooper())

    private val timerUpdater = object : Runnable {
        override fun run() {
            completeMediaPlayer()
            if (!mediaPlayerHandler.isMediaPlayerComplete) {
                updateTrackTimer()
                handler.postDelayed(this, UPDATE_POSITION_DELAY)
            }
        }
    }

    fun playbackControl(playerState: MediaPlayerState): MediaPlayerState {
        val mediaPlayerState = mediaPlayerHandler.playbackControl(playerState)
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
        mediaPlayerHandler.preparePlayer(trackPreviewUrl)
        renderState(PlayerState.Prepared)
        return if (mediaPlayerHandler.isMediaPlayerPrepared) {
            MediaPlayerState.STATE_PREPARED
        } else MediaPlayerState.STATE_DEFAULT
    }

    fun releaseMediaPlayer() {
        mediaPlayerHandler.releasePlayer()
    }

    fun completeMediaPlayer() {
        if (mediaPlayerHandler.isMediaPlayerComplete) {
            timerUpdater.let { handler.removeCallbacks(it) }
            renderState(PlayerState.Complete)
        }
    }

    private fun updateTrackTimer() {
        val trackPosition = mediaPlayerHandler.getTrackPosition().toLong().convertLongToTimeMillis()
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