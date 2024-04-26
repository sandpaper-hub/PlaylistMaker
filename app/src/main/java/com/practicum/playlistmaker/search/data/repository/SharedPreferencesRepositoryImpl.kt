package com.practicum.playlistmaker.search.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.search.NEW_HISTORY_ITEM_KEY
import com.practicum.playlistmaker.search.SHARED_PREFERENCES_HISTORY_FILE
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SharedPreferencesRepository
import com.practicum.playlistmaker.util.toDto

class SharedPreferencesRepositoryImpl(context: Context) :
    SharedPreferencesRepository {

    private val sharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES_HISTORY_FILE,
        AppCompatActivity.MODE_PRIVATE
    )

    override fun saveArrayListToHistory(arrayList: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(NEW_HISTORY_ITEM_KEY, Gson().toJson(arrayList.toDto()))
            .apply()
    }

    override fun getHistoryJson(): String? {
        return sharedPreferences.getString(NEW_HISTORY_ITEM_KEY, null)
    }
}