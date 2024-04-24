package com.practicum.playlistmaker.application.data.repository

import android.content.Context
import androidx.core.content.edit
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.util.GlobalConstants

class AppThemeRepositoryImpl(context: Context) : AppThemeRepository {
    private val sharedPreferences = context.getSharedPreferences(
        GlobalConstants.SHARED_PREFERENCES_THEME_KEY,
        Context.MODE_PRIVATE
    )

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(GlobalConstants.DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(value: Boolean) {
        sharedPreferences.edit { putBoolean(GlobalConstants.DARK_THEME_KEY, value) }
    }
}