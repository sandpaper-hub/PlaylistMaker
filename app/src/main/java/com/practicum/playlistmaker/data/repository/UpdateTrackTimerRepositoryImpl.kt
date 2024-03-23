package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.convertLongToTimeMillis
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.domain.repository.UpdateTrackTimerRepository

class UpdateTrackTimerRepositoryImpl(
    private val mediaPlayerListener: MediaPlayerListener,
    private val mediaPlayer: MediaPlayer
): UpdateTrackTimerRepository {

    private var handler: Handler = Handler(Looper.getMainLooper())

    companion object{
        private const val UPDATE_POSITION_DELAY = 250L
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val position = mediaPlayer.currentPosition
            mediaPlayerListener.onTrackPositionChanged(position.toLong().convertLongToTimeMillis())
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