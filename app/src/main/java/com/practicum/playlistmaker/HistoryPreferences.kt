package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class HistoryPreferences(private val sharedPreferences: SharedPreferences) {

    fun addTrack(historyArrayList: ArrayList<Track>, track: Track) {
        val json =
            sharedPreferences.getString(SharedPreferencesData.newHistoryItemKey, null)
        if (json == null) {
            historyArrayList.add(track)
            sharedPreferences.edit().putString(
                SharedPreferencesData.newHistoryItemKey,
                createJsonFromArrayList(historyArrayList)
            ).apply()
        } else {
            val fromJsonHistoryArrayList = createArrayListFromJson(json)
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
                SharedPreferencesData.newHistoryItemKey,
                createJsonFromArrayList(fromJsonHistoryArrayList)
            ).apply()
        }
    }

    fun clearData() {
        val json = sharedPreferences.getString(SharedPreferencesData.newHistoryItemKey, null)
        if (json != null) {
            val array = createArrayListFromJson(json)
            array.clear()
            sharedPreferences.edit()
                .putString(SharedPreferencesData.newHistoryItemKey, createJsonFromArrayList(array))
                .apply()
        }
    }

    private fun createJsonFromArrayList(arrayList: ArrayList<Track>): String {
        return Gson().toJson(arrayList)
    }

    fun createArrayListFromJson(json: String): ArrayList<Track> {
        val array = Gson().fromJson(json, Array<Track>::class.java)
        return ArrayList(array.toList())
    }
}