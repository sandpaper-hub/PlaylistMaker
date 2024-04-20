package com.practicum.playlistmaker.settings.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(this[APPLICATION_KEY] as Application) }
        }
    }

    private val darkThemeInteractor = Creator.provideDarkThemeInteractor(getApplication())

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