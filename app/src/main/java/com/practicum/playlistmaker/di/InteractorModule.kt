package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.application.domain.interactor.DarkThemeInteractorImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.interactor.MediaPlayerListener
import org.koin.dsl.binds
import org.koin.dsl.module

val interactorModule = module {

    /* создание DarkThemeInteractor */
    single<DarkThemeInteractor> {
        DarkThemeInteractorImpl(get())
    }

    factory <MediaPlayerInteractor>{
        MediaPlayerInteractorImpl(get())
    } binds(arrayOf(MediaPlayerInteractor::class, MediaPlayerListener::class))
}