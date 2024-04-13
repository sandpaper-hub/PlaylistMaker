package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.presentation.TracksSearchPresenter

class App : Application() {
    private var darkTheme = false
    private lateinit var sharedPreferences: SharedPreferences
    var tracksSearchPresenter: TracksSearchPresenter? = null

    override fun onCreate() {
        super.onCreate()
        Creator.initializeCreatorValues(this)
        sharedPreferences =
            getSharedPreferences(SharedPreferencesData.SHARED_PREFERENCES_THEME_KEY, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(SharedPreferencesData.DARK_THEME_KEY, false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit().putBoolean(SharedPreferencesData.DARK_THEME_KEY, darkTheme).apply()
    }
}