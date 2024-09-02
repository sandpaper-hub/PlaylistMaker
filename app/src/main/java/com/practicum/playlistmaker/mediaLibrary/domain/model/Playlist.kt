package com.practicum.playlistmaker.mediaLibrary.domain.model

data class Playlist (
    val id: Int,
    val playlistName: String,
    val playlistDescription: String,
    var playlistCover: String?,
    val tracksId: String,
    val tracksCount: Int
)