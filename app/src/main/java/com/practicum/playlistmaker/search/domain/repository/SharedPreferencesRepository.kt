package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.data.dto.TrackDto

interface SharedPreferencesRepository {
    fun save(arrayList: ArrayList<TrackDto>)
    fun getData(): String?
}