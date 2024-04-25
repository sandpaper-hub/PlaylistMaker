package com.practicum.playlistmaker.application.data.repository

import android.content.Context
import androidx.core.content.edit
import com.practicum.playlistmaker.application.DARK_THEME_KEY
import com.practicum.playlistmaker.application.SHARED_PREFERENCES_THEME_KEY
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository

class AppThemeRepositoryImpl(context: Context) : AppThemeRepository {
    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_THEME_KEY,
        Context.MODE_PRIVATE
    )

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(value: Boolean) {
        sharedPreferences.edit { putBoolean(DARK_THEME_KEY, value) }
    }
}