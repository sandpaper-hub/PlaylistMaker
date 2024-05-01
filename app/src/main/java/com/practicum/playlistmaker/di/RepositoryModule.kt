package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.application.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.search.data.repository.SharedPreferencesRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    /* создание AppThemeRepository */
    single<AppThemeRepository> {
        AppThemeRepositoryImpl(get())
    }

    single<SharedPreferencesRepository> {
        SharedPreferencesRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }
}