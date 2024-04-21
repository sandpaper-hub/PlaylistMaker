package com.practicum.playlistmaker.util

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.util.TypedValue
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

inline fun <reified T : Serializable> Intent.getSerializableTrack(key: String): T? = when {
    SDK_INT >= 33 -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

fun List<TrackDto>.toTrackList(): ArrayList<Track> {
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

fun Float.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics
    ).toInt()
}

fun String.createArrayListFromJson(): ArrayList<Track> {
    val array = Gson().fromJson(this, Array<Track>::class.java)
    return ArrayList(array.toList())
}

fun Track.hasNullableData(): Boolean {
    return this.trackDuration == null || this.previewUrl == null
}