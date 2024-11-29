package com.practicum.playlistmaker.mediaLibrary.domain.model

import java.io.Serializable

data class Playlist (
    val id: Int,
    val playlistName: String,
    val playlistDescription: String,
    var playlistCover: String?,
    var tracksId: String?,
    var tracksCount: Int
): Serializable