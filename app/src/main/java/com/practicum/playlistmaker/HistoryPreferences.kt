package com.practicum.playlistmaker

import android.content.SharedPreferences

class HistoryPreferences(private val sharedPreferences: SharedPreferences) {

    fun addTrack(historyArrayList: ArrayList<Track>, track: Track) {
        val json =
            sharedPreferences.getString(SharedPreferencesData.newHistoryItemKey, null)
        if (json == null) {
            historyArrayList.add(track)
            sharedPreferences.edit().putString(
                SharedPreferencesData.newHistoryItemKey,
                Transformer.createJsonFromArrayList(historyArrayList)
            ).apply()
        } else {
            val fromJsonHistoryArrayList = Transformer.createArrayListFromJson(json)
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
                Transformer.createJsonFromArrayList(fromJsonHistoryArrayList)
            ).apply()
        }
    }

    fun clearData() {
        val json = sharedPreferences.getString(SharedPreferencesData.newHistoryItemKey, null)
        if (json != null) {
            val array = Transformer.createArrayListFromJson(json)
            array.clear()
            sharedPreferences.edit()
                .putString(SharedPreferencesData.newHistoryItemKey, Transformer.createJsonFromArrayList(array))
                .apply()
        }
    }
}