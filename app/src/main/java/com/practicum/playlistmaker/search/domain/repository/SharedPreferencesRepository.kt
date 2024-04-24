package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.data.dto.TrackDto

interface SharedPreferencesRepository {
    fun saveArrayListToHistory(arrayList: ArrayList<TrackDto>)
    fun getHistoryJson(): String?
}