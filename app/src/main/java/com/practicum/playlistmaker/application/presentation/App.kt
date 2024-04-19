package com.practicum.playlistmaker.application.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.application.presentation.interactor.DarkThemeInteractor
import com.practicum.playlistmaker.creator.Creator

class App : Application() {
    private var darkTheme = false
    private lateinit var darkThemeInteractor: DarkThemeInteractor

    override fun onCreate() {
        super.onCreate()
        Creator.initializeCreatorValues(this)
        darkThemeInteractor = Creator.provideDarkThemeInteractor(this)
        darkTheme = darkThemeInteractor.getThemeValue()
        setDefaultNightMode()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        setDefaultNightMode()
        darkThemeInteractor.saveThemeValue(darkTheme)
    }

    private fun setDefaultNightMode() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}