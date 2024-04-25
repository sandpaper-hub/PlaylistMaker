package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track

fun List<TrackDto>.toDomain(): ArrayList<Track> {
    val resultList = ArrayList<Track>()
    for (trackDto in this) {
        val track = Track(
            trackDto.trackId,
            trackDto.trackName,
            trackDto.artistName,
            trackDto.trackTimeMillis,
            trackDto.artworkUrl100,
            trackDto.collectionName,
            trackDto.releaseDate,
            trackDto.primaryGenreName,
            trackDto.country,
            trackDto.previewUrl
        )
        resultList.add(track)
    }
    return resultList
}