package com.practicum.playlistmaker.mediaLibrary.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCover: String?,
    var tracksId: String?,
    var tracksCount: Int
)