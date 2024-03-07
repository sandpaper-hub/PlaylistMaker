package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class HistoryPreferences(private val sharedPreferences: SharedPreferences) {

    fun addTrack(historyArrayList: ArrayList<Track>, track: Track) {
        val json =
            sharedPreferences.getString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, null)
        if (json == null) {
            historyArrayList.add(track)
            sharedPreferences.edit().putString(
                SharedPreferencesData.NEW_HISTORY_ITEM_KEY,
                Gson().toJson(historyArrayList)
            ).apply()
        } else {
            val fromJsonHistoryArrayList = json.createArrayListFromJson()
            if (fromJsonHistoryArrayList.size <= 10) {
                if (fromJsonHistoryArrayList.contains(track)) {
                    fromJsonHistoryArrayList.remove(track)
                }
                if (fromJsonHistoryArrayList.size == 10) {
                    fromJsonHistoryArrayList.removeAt(9)
                }
                fromJsonHistoryArrayList.add(0, track)
            }
            sharedPreferences.edit().putString(
                SharedPreferencesData.NEW_HISTORY_ITEM_KEY,
                Gson().toJson(fromJsonHistoryArrayList)
            ).apply()
        }
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