package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import android.os.Handler
import com.practicum.playlistmaker.domain.TrackPositionListener
import com.practicum.playlistmaker.domain.repository.UpdateTrackTimerRepository

class UpdateTrackTimerRepositoryImpl(
    private val trackPositionListener: TrackPositionListener,
    private val handler: Handler,
    private val mediaPlayer: MediaPlayer
): UpdateTrackTimerRepository {
    companion object{
        private const val UPDATE_POSITION_DELAY = 250L
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val position = mediaPlayer.currentPosition
            trackPositionListener.onTrackPositionChanged(position)
            handler.postDelayed(this, UPDATE_POSITION_DELAY)
        }

    }

    override fun startUpdatingTrackPosition() {
        stopUpdatingTrackPosition()
        handler.post(runnable)
    }

    override fun stopUpdatingTrackPosition() {
        runnable.let { handler.removeCallbacks(it) }
    }
}