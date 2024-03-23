package com.practicum.playlistmaker

import android.content.Intent
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.handler.MediaPlayerHandlerImpl
import com.practicum.playlistmaker.data.repository.TrackInfoRepositoryImpl
import com.practicum.playlistmaker.data.repository.UpdateTrackTimerRepositoryImpl
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.domain.repository.TrackInfoRepository
import com.practicum.playlistmaker.domain.repository.UpdateTrackTimerRepository
import com.practicum.playlistmaker.presentation.usecase.GetTrackInfoUseCase
import com.practicum.playlistmaker.presentation.usecase.PreparePlayerUseCase
import com.practicum.playlistmaker.domain.usecase.GetTrackInfoUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.PlaybackControlUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.PreparePlayerUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.ReleasePlayerUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.UpdateTrackTimerUseCaseImpl
import com.practicum.playlistmaker.presentation.usecase.PlaybackControlUseCase
import com.practicum.playlistmaker.presentation.usecase.ReleasePlayerUseCase
import com.practicum.playlistmaker.presentation.usecase.UpdateTrackTimerUseCase

object Creator {
    private lateinit var mediaPlayerHandler: MediaPlayerHandlerImpl

    private fun getTrackInfoRepository(intent: Intent): TrackInfoRepository {
        return TrackInfoRepositoryImpl(intent)
    }

    fun getTrackInfoUseCase(intent: Intent): GetTrackInfoUseCase {
        return GetTrackInfoUseCaseImpl(getTrackInfoRepository(intent))
    }

    private fun getMediaPlayerHandler(
        playerListener: MediaPlayerListener,
        trackPreviewUrl: String?
    ): MediaPlayerHandler {
        mediaPlayerHandler = MediaPlayerHandlerImpl(playerListener, trackPreviewUrl)
        return mediaPlayerHandler
    }

    fun getPreparePlayerUseCase(
        playerListener: MediaPlayerListener,
        trackPreviewUrl: String?
    ): PreparePlayerUseCase {
        return PreparePlayerUseCaseImpl(getMediaPlayerHandler(playerListener, trackPreviewUrl))
    }

    fun getPlaybackControlUseCase(): PlaybackControlUseCase {
        return PlaybackControlUseCaseImpl(mediaPlayerHandler)
    }

    fun getReleasePlayerUseCase(): ReleasePlayerUseCase {
        return ReleasePlayerUseCaseImpl(mediaPlayerHandler)
    }

    private fun getUpdateTrackTimerRepositoryImpl(
        mediaPlayerListener: MediaPlayerListener,
        mediaPlayer: MediaPlayer
    ): UpdateTrackTimerRepository {
        return UpdateTrackTimerRepositoryImpl(mediaPlayerListener, mediaPlayer)
    }

    fun getUpdateTrackTimerUseCase(
        mediaPlayerListener: MediaPlayerListener,
    ): UpdateTrackTimerUseCase {
        return UpdateTrackTimerUseCaseImpl(
            getUpdateTrackTimerRepositoryImpl(
                mediaPlayerListener,
                mediaPlayerHandler.mediaPlayer
            )
        )
    }
}