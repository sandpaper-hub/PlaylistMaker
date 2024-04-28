package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val SHARED_PREFERENCES_THEME_KEY = "PlaylistMakerThemePreferences"

val dataModule = module {
    /* создание SharedPreferences */
    single<SharedPreferences> {
        androidContext().getSharedPreferences(SHARED_PREFERENCES_THEME_KEY, Context.MODE_PRIVATE)
    }




}