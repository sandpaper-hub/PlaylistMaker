package com.practicum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.NEW_HISTORY_ITEM_KEY
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.util.toDto

class SharedPreferencesRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SharedPreferencesRepository {

    override fun saveArrayListToHistory(arrayList: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(NEW_HISTORY_ITEM_KEY, Gson().toJson(arrayList.toDto()))
            .apply()
    }

    override fun getHistoryJson(): String? {
        return sharedPreferences.getString(NEW_HISTORY_ITEM_KEY, null)
    }
}