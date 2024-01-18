package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackName: String,
    val artistName: String,
    @SerializedName ("trackTimeMillis") val trackDuration: Long,
    val artworkUrl100: String
)
