package com.practicum.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import java.lang.StringBuilder

class HistoryPreferences(private val sharedPreferences: SharedPreferences) {

    fun addTrack(track: Track) {
        val json =
            sharedPreferences.getString(SharedPreferencesData.NEW_HISTORY_ITEM_KEY, null)
        if (json == null) {
            val newHistoryArrayList = ArrayList<Track>().add(track)
            sharedPreferences.edit().putString(
                SharedPreferencesData.NEW_HISTORY_ITEM_KEY,
                Gson().toJson(newHistoryArrayList)
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
            Log.d("PREFERENCESDATA", fromJsonHistoryArrayList.print())
            Log.d("PREFERENCESDATA", "Add")
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

    fun ArrayList<Track>.print(): String {
        var string = StringBuilder()
        for ((number, value) in this.withIndex()) {
            string.append("$number $value\n")
        }
        return string.toString()
    }
}