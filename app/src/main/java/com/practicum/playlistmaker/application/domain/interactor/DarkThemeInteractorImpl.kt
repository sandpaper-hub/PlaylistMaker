package com.practicum.playlistmaker.application.domain.interactor

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository

class DarkThemeInteractorImpl(private val themeRepository: AppThemeRepository) :
    DarkThemeInteractor {
    override fun getThemeValue(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun saveThemeValue(value: Boolean) {
        themeRepository.setDarkThemeEnabled(value)
    }

    override fun switchTheme(isEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveThemeValue(isEnabled)
    }

}