package com.practicum.playlistmaker.settings.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel(private val darkThemeInteractor: DarkThemeInteractor) : ViewModel() {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            val darkThemeInteractor = Creator.provideDarkThemeInteractor()
            initializer { SettingsViewModel(darkThemeInteractor) }
        }
    }

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