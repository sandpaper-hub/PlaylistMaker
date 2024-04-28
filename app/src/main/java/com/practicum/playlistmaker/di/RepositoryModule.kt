package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.application.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import org.koin.dsl.module

val repositoryModule = module {
    /* создание AppThemeRepository */
    single<AppThemeRepository> {
        AppThemeRepositoryImpl(get())
    }
}