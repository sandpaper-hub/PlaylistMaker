package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.application.domain.interactor.DarkThemeInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    /* создание DarkThemeInteractor */
    single<DarkThemeInteractor> {
        DarkThemeInteractorImpl(get())
    }
}