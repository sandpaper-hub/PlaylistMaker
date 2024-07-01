package com.practicum.playlistmaker.mediaLibrary.data.converters

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.convertLongToTimeMillis
import com.practicum.playlistmaker.util.convertStringToLongMillis

class TrackDbConverter {
    fun map(track: TrackDto): TrackEntity {
        return TrackEntity(
            track.trackId ?: "",
            track.artworkUrl100 ?: "",
            track.trackName ?: "",
            track.artistName ?: "",
            track.collectionName ?: "",
            track.releaseDate ?: "",
            track.primaryGenreName ?: "",
            track.country ?: "",
            track.trackTimeMillis?.convertLongToTimeMillis() ?: "",
            track.previewUrl ?: ""
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.id,
            track.trackName,
            track.artist,
            track.duration.convertStringToLongMillis(),
            track.coverUrl,
            track.album,
            track.releaseYear,
            track.genre,
            track.country,
            track.trackUrl
        )
    }
    fun convertFromTracksEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> map(track) }
    }
}