package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.application.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.application.domain.interactor.DarkThemeInteractorImpl
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.application.presentation.interactor.DarkThemeInteractor
import com.practicum.playlistmaker.player.data.impl.MediaPlayerWrapperImpl
import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.wrapper.MediaPlayerWrapper
import com.practicum.playlistmaker.search.data.repository.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.player.presentation.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.repository.SharedPreferencesRepository
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.presentation.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl

object Creator {
    private lateinit var application: Application
    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    fun initializeCreatorValues(application: Application) {
        Creator.application = application
        sharedPreferencesRepository = getSharedPreferencesRepository(application)
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    private fun getSharedPreferencesRepository(context: Context): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(context)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context), sharedPreferencesRepository)
    }

    fun provideMediaPlayerInteractor(
    ): MediaPlayerInteractor {
       return MediaPlayerInteractorImpl()
    }
    fun provideMediaPlayerWrapper(): MediaPlayerWrapper {
        return MediaPlayerWrapperImpl()
    }

    private fun getAppThemeRepository(context: Context): AppThemeRepository {
        return AppThemeRepositoryImpl(context)
    }

    fun provideDarkThemeInteractor(context: Context): DarkThemeInteractor {
        return DarkThemeInteractorImpl(getAppThemeRepository(context))
    }
}