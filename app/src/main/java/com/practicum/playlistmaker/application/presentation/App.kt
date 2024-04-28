package com.practicum.playlistmaker.application.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.di.wrapperModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private val darkThemeInteractor by inject<DarkThemeInteractor>()

    override fun onCreate() {
        super.onCreate()
        Creator.initializeCreatorValues(this)
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, wrapperModule, interactorModule, viewModelModule )
        }
        setDefaultNightMode(darkThemeInteractor.getThemeValue())
    }

    private fun setDefaultNightMode(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}