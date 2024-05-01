package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.search.SHARED_PREFERENCES_HISTORY_FILE
import com.practicum.playlistmaker.search.data.network.ITunesSearchApi
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val SHARED_PREFERENCES_THEME_KEY = "PlaylistMakerThemePreferences"
private const val baseUrl = "https://itunes.apple.com"
val themeQualifier = named("theme")
val historyQualifier = named("history")


val dataModule = module {
    /* создание SharedPreferences */
    single<SharedPreferences> (themeQualifier) {
        androidContext().getSharedPreferences(
            SHARED_PREFERENCES_THEME_KEY,
            Context.MODE_PRIVATE
        )
    }

    single<SharedPreferences>(historyQualifier) {
        androidContext().getSharedPreferences(
            SHARED_PREFERENCES_HISTORY_FILE,
            Context.MODE_PRIVATE
        )
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single<ITunesSearchApi> {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }
}