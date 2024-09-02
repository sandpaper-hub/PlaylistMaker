package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.application.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.converters.TrackDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.repository.CreatePlaylistRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.data.repository.FavoriteTracksTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.data.repository.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistRepository
import com.practicum.playlistmaker.mediaLibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.mediaLibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.repository.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import org.koin.android.ext.koin.androidContext
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

    factory<PlaylistDbConverter> {
        PlaylistDbConverter()
    }
    single<MediaPlayerRepository> {
        PlayerRepositoryImpl(get(), get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksTracksRepositoryImpl(get(), get())
    }

    single<CreatePlaylistRepository> {
        CreatePlaylistRepositoryImpl(androidContext(), get(), get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get())
    }
}