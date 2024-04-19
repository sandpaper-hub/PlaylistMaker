package com.practicum.playlistmaker.application.data.repository

import android.content.Context
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.util.GlobalConstants

class AppThemeRepositoryImpl(context: Context) : AppThemeRepository {
    private val sharedPreferences = context.getSharedPreferences(
        GlobalConstants.SHARED_PREFERENCES_THEME_KEY,
        Context.MODE_PRIVATE
    )

    override fun getData(): Boolean {
        return sharedPreferences.getBoolean(GlobalConstants.DARK_THEME_KEY, false)
    }

    override fun saveData(value: Boolean) {
        sharedPreferences.edit().putBoolean(GlobalConstants.DARK_THEME_KEY, value).apply()
    }
}