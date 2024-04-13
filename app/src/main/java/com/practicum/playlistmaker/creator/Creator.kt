package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.handler.MediaPlayerHandlerImpl
import com.practicum.playlistmaker.player.data.repository.UpdateTrackTimerRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.player.data.listeners.MediaPlayerListener
import com.practicum.playlistmaker.player.domain.handler.MediaPlayerHandler
import com.practicum.playlistmaker.player.domain.repository.UpdateTrackTimerRepository
import com.practicum.playlistmaker.search.domain.repository.SharedPreferencesRepository
import com.practicum.playlistmaker.player.presentation.PreparePlayerUseCase
import com.practicum.playlistmaker.player.domain.useCases.PlaybackControlUseCaseImpl
import com.practicum.playlistmaker.player.domain.useCases.PreparePlayerUseCaseImpl
import com.practicum.playlistmaker.player.domain.useCases.ReleasePlayerUseCaseImpl
import com.practicum.playlistmaker.player.domain.useCases.UpdateTrackTimerUseCaseImpl
import com.practicum.playlistmaker.player.presentation.PlaybackControlUseCase
import com.practicum.playlistmaker.player.presentation.ReleasePlayerUseCase
import com.practicum.playlistmaker.player.presentation.UpdateTrackTimerUseCase
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.presentation.TracksSearchPresenter

object Creator {
    private lateinit var application: Application
    private lateinit var mediaPlayerHandler: MediaPlayerHandlerImpl
    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    fun initializeCreatorValues(application: Application) {
        Creator.application = application
        sharedPreferencesRepository = getSharedPreferencesRepository(application)
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context), sharedPreferencesRepository)
    }

    fun provideTracksSearchPresenter(context: Context): TracksSearchPresenter {
        return TracksSearchPresenter(context)
    }

    private fun getSharedPreferencesRepository(context: Context): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(context)
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