package com.practicum.playlistmaker.settings.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor

class SettingsViewModel(private val darkThemeInteractor: DarkThemeInteractor) : ViewModel() {

    fun isChecked(): Boolean {
        return darkThemeInteractor.getThemeValue()
    }

    fun switchTheme(isChecked:Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        darkThemeInteractor.saveThemeValue(isChecked)
    }
}