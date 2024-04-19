package com.practicum.playlistmaker.search.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.util.GlobalConstants
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SharedPreferencesRepository

class SharedPreferencesRepositoryImpl(context: Context) :
    SharedPreferencesRepository {

    private val sharedPreferences = context.getSharedPreferences(
        GlobalConstants.SHARED_PREFERENCES_HISTORY_FILE,
        AppCompatActivity.MODE_PRIVATE
    )

    override fun save(arrayList: ArrayList<Track>) {
        sharedPreferences.edit()
            .putString(GlobalConstants.NEW_HISTORY_ITEM_KEY, Gson().toJson(arrayList))
            .apply()
    }

    override fun getData(): String? {
        return sharedPreferences.getString(GlobalConstants.NEW_HISTORY_ITEM_KEY, null)
    }
}