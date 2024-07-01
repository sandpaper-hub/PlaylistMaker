package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.application.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.repository.FavoriteTracksTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.api.FavoriteTracksRepository
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.repository.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    /* создание AppThemeRepository */
    single <AppThemeRepository> {
        AppThemeRepositoryImpl(get(themeQualifier))
    }

    single<SharedPreferencesRepository> {
        SharedPreferencesRepositoryImpl(get(historyQualifier))
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    factory<TrackDbConverter> {
        TrackDbConverter()
    }

    single<MediaPlayerRepository> {
        PlayerRepositoryImpl(get(), get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksTracksRepositoryImpl(get(), get())
    }
}