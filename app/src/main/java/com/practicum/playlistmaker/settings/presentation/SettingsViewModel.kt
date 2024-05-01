package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor

class SettingsViewModel(private val darkThemeInteractor: DarkThemeInteractor) : ViewModel() {

    fun isChecked(): Boolean {
        return darkThemeInteractor.getThemeValue()
    }

    fun switchTheme(isChecked:Boolean) {
        darkThemeInteractor.switchTheme(isChecked)
        darkThemeInteractor.saveThemeValue(isChecked)
    }
}