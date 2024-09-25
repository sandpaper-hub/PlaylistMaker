package com.practicum.playlistmaker.util

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.TypedValue
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

inline fun <reified T : Serializable> Bundle.getSerializableTrack(key: String): T? = when {
    SDK_INT >= 33 -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

fun ArrayList<Track>.toDto(): ArrayList<TrackDto> {
    val resultList = ArrayList<TrackDto>()
    for (track in this) {
        val trackDto = TrackDto(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackDuration,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
        resultList.add(trackDto)
    }
    return resultList
}

fun Long.convertLongToTimeMillis(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}

fun String.convertStringToLongMillis(): Long {
    val parts = this.split(":")
    val minutes = parts[0].toLong()
    val seconds = parts[1].toLong()
    return (minutes * 60 + seconds) * 1000
}

fun Float.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
    ).toInt()
}

fun String.createTrackArrayListFromJson(): ArrayList<Track> {
    val array = Gson().fromJson(this, Array<Track>::class.java)
    return ArrayList(array.toList())
}

fun String.createPlaylistIdsArrayListFromJson(): ArrayList<String> {
    val array = Gson().fromJson(this, Array<String>::class.java)
    return ArrayList(array.toList())
}

fun Track.hasNullableData(): Boolean {
    return this.trackDuration == null || this.previewUrl == null
}

fun Int.declineTracksCount(): String {
    return when {
        this % 10 == 1 && this % 100 != 11 -> "трек"
        this % 10 in 2..4 && (this % 100 !in 12..14) -> "трека"
        else -> "треков"
    }
}