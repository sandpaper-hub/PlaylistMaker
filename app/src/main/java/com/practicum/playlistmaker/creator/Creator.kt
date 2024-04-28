package com.practicum.playlistmaker.creator

import android.app.Application
import com.practicum.playlistmaker.player.data.impl.MediaPlayerWrapperImpl
import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.wrapper.MediaPlayerWrapper
import com.practicum.playlistmaker.search.data.repository.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractorImpl

object Creator {
    private lateinit var application: Application
    private lateinit var sharedPreferencesRepository: SharedPreferencesRepository
    fun initializeCreatorValues(application: Application) {
        this.application = application
        sharedPreferencesRepository = getSharedPreferencesRepository()
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(application))
    }

    private fun getSharedPreferencesRepository(): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(application)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(), sharedPreferencesRepository)
    }

    fun provideMediaPlayerInteractor(
    ): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(provideMediaPlayerWrapper())
    }

    private fun provideMediaPlayerWrapper(): MediaPlayerWrapper {
        return MediaPlayerWrapperImpl()
    }
}