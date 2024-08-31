package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.application.domain.interactor.DarkThemeInteractorImpl
import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.impl.CreatePlaylistInteractorImpl
import com.practicum.playlistmaker.mediaLibrary.domain.impl.FavoriteTracksInteractorImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerListener
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractorImpl
import org.koin.dsl.binds
import org.koin.dsl.module

val interactorModule = module {

    /* создание DarkThemeInteractor */
    single<DarkThemeInteractor> {
        DarkThemeInteractorImpl(get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get(), get())
    } binds (arrayOf(MediaPlayerInteractor::class, MediaPlayerListener::class))

    single<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<CreatePlaylistInteractor> {
        CreatePlaylistInteractorImpl(get())
    }
}