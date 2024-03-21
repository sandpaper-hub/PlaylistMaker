package com.practicum.playlistmaker

import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import com.practicum.playlistmaker.data.handler.MediaPlayerHandlerImpl
import com.practicum.playlistmaker.data.repository.TrackInfoRepositoryImpl
import com.practicum.playlistmaker.data.repository.UpdateTrackTimerRepositoryImpl
import com.practicum.playlistmaker.domain.TrackPositionListener
import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.domain.repository.TrackInfoRepository
import com.practicum.playlistmaker.domain.repository.UpdateTrackTimerRepository
import com.practicum.playlistmaker.presentation.usecase.GetTrackInfoUseCase
import com.practicum.playlistmaker.presentation.usecase.PreparePlayerUseCase
import com.practicum.playlistmaker.domain.usecase.GetTrackInfoUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.PlaybackControlUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.PreparePlayerUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.UpdateTrackTimerUseCaseImpl
import com.practicum.playlistmaker.presentation.usecase.PlaybackControlUseCase
import com.practicum.playlistmaker.presentation.usecase.UpdateTrackTimerUseCase

class Creator {
    private lateinit var mediaPlayerHandler: MediaPlayerHandler
    private fun getTrackInfoRepository(intent: Intent): TrackInfoRepository {
        return TrackInfoRepositoryImpl(intent)
    }

    fun getTrackInfoUseCase(intent: Intent): GetTrackInfoUseCase {
        return GetTrackInfoUseCaseImpl(getTrackInfoRepository(intent))
    }

    private fun getMediaPlayerHandler(
        mediaPlayer: MediaPlayer,
        trackPreviewUrl: String?
    ): MediaPlayerHandler {
        mediaPlayerHandler = MediaPlayerHandlerImpl(mediaPlayer, trackPreviewUrl)
        return mediaPlayerHandler
    }

    fun getPreparePlayerUseCase(
        mediaPlayer: MediaPlayer,
        trackPreviewUrl: String?
    ): PreparePlayerUseCase {
        return PreparePlayerUseCaseImpl(getMediaPlayerHandler(mediaPlayer, trackPreviewUrl))
    }

    fun getPlaybackControlUseCase(): PlaybackControlUseCase {
        return PlaybackControlUseCaseImpl(mediaPlayerHandler)
    }

    private fun getUpdateTrackTimerRepositoryImpl(
        trackPositionListener: TrackPositionListener,
        handler: Handler,
        mediaPlayer: MediaPlayer
    ): UpdateTrackTimerRepository {
        return UpdateTrackTimerRepositoryImpl(trackPositionListener, handler, mediaPlayer)
    }

    fun getUpdateTrackTimerUseCase(
        trackPositionListener: TrackPositionListener,
        handler: Handler,
        mediaPlayer: MediaPlayer
    ): UpdateTrackTimerUseCase {
        return UpdateTrackTimerUseCaseImpl(
            getUpdateTrackTimerRepositoryImpl(
                trackPositionListener,
                handler,
                mediaPlayer
            )
        )
    }
}