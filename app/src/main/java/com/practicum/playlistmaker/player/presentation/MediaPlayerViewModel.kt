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
import com.practicum.playlistmaker.util.convertLongToTimeMillis
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

    fun createPlayer(): MediaPlayerState {
        renderState(PlayerState.Created)
        return MediaPlayerState.STATE_DEFAULT
    }

    fun preparePlayer(trackPreviewUrl: String?): MediaPlayerState {
        mediaPlayerInteractor.preparePlayer(trackPreviewUrl)
        return if (mediaPlayerInteractor.isMediaPlayerPrepared) {
            renderState(PlayerState.Prepared)
            MediaPlayerState.STATE_PREPARED
        } else MediaPlayerState.STATE_DEFAULT
    }

    fun playbackControl(playerState: MediaPlayerState): MediaPlayerState {
        return when (playerState) {
            MediaPlayerState.STATE_PREPARED, MediaPlayerState.STATE_PAUSED -> {
                mediaPlayerInteractor.startPlayer()
                renderState(PlayerState.Playing)
                timerUpdater.let { handler.post(it) }
                MediaPlayerState.STATE_PLAYING
            }

            MediaPlayerState.STATE_PLAYING -> {
                mediaPlayerInteractor.pausePlayer()
                renderState(PlayerState.Pause)
                timerUpdater.let { handler.removeCallbacks(it) }
                MediaPlayerState.STATE_PAUSED
            }

            MediaPlayerState.STATE_DEFAULT -> MediaPlayerState.STATE_PREPARED
        }
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
        renderState(
            PlayerState.ChangePosition(
                mediaPlayerInteractor.getTrackPosition().toLong().convertLongToTimeMillis()
            )
        )
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        timerUpdater.let { handler.removeCallbacks(it) }
    }

}