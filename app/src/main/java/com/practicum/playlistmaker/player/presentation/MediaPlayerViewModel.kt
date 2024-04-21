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

    private lateinit var state: PlayerState

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

    fun createPlayer(): PlayerState {
        state = PlayerState.Created
        renderState(state)
        return state
    }

    fun preparePlayer(trackPreviewUrl: String?): PlayerState {
        mediaPlayerInteractor.preparePlayer(trackPreviewUrl)
        state = PlayerState.Prepared
        return if (mediaPlayerInteractor.isMediaPlayerPrepared) {
            renderState(state)
            state
        } else PlayerState.Created
    }

    fun playbackControl(playerState: PlayerState): PlayerState {
        return when (playerState) {
            is PlayerState.Prepared, PlayerState.Pause -> {
                state = PlayerState.Playing
                mediaPlayerInteractor.startPlayer()
                renderState(state)
                timerUpdater.let { handler.post(it) }
                state
            }

            is PlayerState.Playing -> {
                state = PlayerState.Pause
                mediaPlayerInteractor.pausePlayer()
                renderState(state)
                timerUpdater.let { handler.removeCallbacks(it) }
                state
            }

            else -> {
                state = PlayerState.Prepared
                state
            }
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