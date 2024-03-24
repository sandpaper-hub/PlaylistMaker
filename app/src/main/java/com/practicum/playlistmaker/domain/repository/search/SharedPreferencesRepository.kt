package com.practicum.playlistmaker.domain.repository.search

import com.practicum.playlistmaker.domain.models.Track

interface SharedPreferencesRepository {
    fun save(arrayList: ArrayList<Track>)
    fun getData(): String?
}