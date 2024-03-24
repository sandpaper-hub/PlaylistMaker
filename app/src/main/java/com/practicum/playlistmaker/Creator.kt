package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.handler.MediaPlayerHandlerImpl
import com.practicum.playlistmaker.data.repository.player.UpdateTrackTimerRepositoryImpl
import com.practicum.playlistmaker.data.repository.search.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.domain.repository.player.UpdateTrackTimerRepository
import com.practicum.playlistmaker.domain.repository.search.SharedPreferencesRepository
import com.practicum.playlistmaker.presentation.usecase.player.PreparePlayerUseCase
import com.practicum.playlistmaker.domain.usecase.player.PlaybackControlUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.player.PreparePlayerUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.player.ReleasePlayerUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.player.UpdateTrackTimerUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.search.AddTrackToHistoryUseCaseImpl
import com.practicum.playlistmaker.domain.usecase.search.ClearHistoryUseCaseImpl
import com.practicum.playlistmaker.presentation.usecase.player.PlaybackControlUseCase
import com.practicum.playlistmaker.presentation.usecase.player.ReleasePlayerUseCase
import com.practicum.playlistmaker.presentation.usecase.player.UpdateTrackTimerUseCase

object Creator {
    private lateinit var application: Application
    private lateinit var mediaPlayerHandler: MediaPlayerHandlerImpl
    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    fun setData(application: Application) {
        this.application = application
        sharedPreferencesRepository = getSharedPreferencesRepository(application)
    }

    private fun getSharedPreferencesRepository(context: Context): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(context)
    }

    fun getAddTrackToHistoryUseCase(): AddTrackToHistoryUseCaseImpl {
        return AddTrackToHistoryUseCaseImpl(sharedPreferencesRepository)
    }
    fun getClearHistoryUseCase(): ClearHistoryUseCaseImpl {
        return ClearHistoryUseCaseImpl(sharedPreferencesRepository)
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