package com.practicum.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "in_playlists_table")
data class TrackInPlaylistsEntity(
    @PrimaryKey
    val id: String,
    val coverUrl: String,
    val trackName: String,
    val artist: String,
    val album: String,
    val releaseYear: String,
    val genre: String,
    val country: String,
    val duration: String,
    val trackUrl: String
)