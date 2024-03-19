package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track

class HistoryPreferences(private val sharedPreferences: SharedPreferences) {

    fun addTrack(track: Track) {
        val json =
            sharedPreferences.getString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, null)
        val historyArray = json?.createArrayListFromJson() ?: ArrayList()
        historyArray.removeIf { it == track }
        historyArray.add(0, track)
        if (historyArray.size > 10) {
            historyArray.removeAt(10)
        }
        sharedPreferences.edit()
            .putString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, Gson().toJson(historyArray))
            .apply()
    }

    fun clearData() {
        val json = sharedPreferences.getString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, null)
        if (json != null) {
            val array = json.createArrayListFromJson()
            array.clear()
            sharedPreferences.edit()
                .putString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, Gson().toJson(array))
                .apply()
        }
    }
}