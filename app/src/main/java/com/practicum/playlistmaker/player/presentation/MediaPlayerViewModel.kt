package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.presentation.model.PlayerState

class MediaPlayerViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) : ViewModel() {

    companion object {
        private const val UPDATE_POSITION_DELAY = 250L
        private const val EMPTY_STRING = ""
    }

    private var isCreated = false
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

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

    private val preparePlayerRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayerInteractor.isMediaPlayerPrepared) {
                renderState(PlayerState.Prepared(mediaPlayerInteractor.getTrackPosition()))
            } else {
                handler.post(this)
            }
        }
    }

    fun createPlayer() {
        renderState(PlayerState.Created)
    }

    fun preparePlayer(trackPreviewUrl: String?) {
        if (!isCreated) {
            mediaPlayerInteractor.preparePlayer(trackPreviewUrl)
            isCreated = true
        }
        preparePlayerRunnable.let { handler.post(it) }
    }

    fun playbackControl() {
        when (stateLiveData.value) {
            is PlayerState.Prepared, PlayerState.Pause, PlayerState.Complete -> {
                mediaPlayerInteractor.startPlayer()
                renderState(PlayerState.Playing)
                timerUpdater.let { handler.post(it) }
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
            timerUpdater.let { handler.removeCallbacks(it) }
        }
    }

    fun completeMediaPlayer() {
        if (mediaPlayerInteractor.isMediaPlayerComplete) {
            timerUpdater.let { handler.removeCallbacks(it) }
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

    private fun renderState(state: PlayerState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        timerUpdater.let { handler.removeCallbacks(it) }
        mediaPlayerInteractor.releasePlayer()
    }
}