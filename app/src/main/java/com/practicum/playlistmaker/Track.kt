package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackDuration: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
)
