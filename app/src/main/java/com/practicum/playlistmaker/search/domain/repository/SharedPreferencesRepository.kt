package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.models.Track

interface SharedPreferencesRepository {
    fun saveArrayListToHistory(arrayList: ArrayList<Track>)
    fun getHistoryJson(): String?
}