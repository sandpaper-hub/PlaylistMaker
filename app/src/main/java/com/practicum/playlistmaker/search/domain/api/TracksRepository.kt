package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<TrackDto>>
}