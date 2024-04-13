package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.models.Track

interface SharedPreferencesRepository {
    fun save(arrayList: ArrayList<Track>)
    fun getData(): String?
}