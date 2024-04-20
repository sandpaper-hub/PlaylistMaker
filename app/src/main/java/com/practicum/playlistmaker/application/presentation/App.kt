package com.practicum.playlistmaker.application.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.application.presentation.interactor.DarkThemeInteractor
import com.practicum.playlistmaker.creator.Creator

class App : Application() {
    private lateinit var darkThemeInteractor: DarkThemeInteractor

    override fun onCreate() {
        super.onCreate()
        Creator.initializeCreatorValues(this)
        darkThemeInteractor = Creator.provideDarkThemeInteractor(this)
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