package com.practicum.playlistmaker.search.data.dto

import com.practicum.playlistmaker.search.domain.models.Track

class TrackSearchResponse (
    val results: List<Track>
): Response()