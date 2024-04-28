package com.practicum.playlistmaker.application.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository

class AppThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : AppThemeRepository {
    companion object{
        const val DARK_THEME_KEY = "isDarkTheme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(value: Boolean) {
        sharedPreferences.edit { putBoolean(DARK_THEME_KEY, value) }
    }
}