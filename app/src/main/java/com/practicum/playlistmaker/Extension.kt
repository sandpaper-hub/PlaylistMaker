package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.TypedValue
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

inline fun <reified T : Parcelable> Intent.getParcelableTrack(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

fun Any.convertLongToTimeMillis(): String {
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

fun Track.hasntNullableData(): Boolean {
    var hasntNullable = true
    if (this.trackId == null) {
        hasntNullable = false
    }
    if (this.trackName == null) {
        hasntNullable = false
    }
    if (this.artistName == null) {
        hasntNullable = false
    }
    if (this.trackDuration == null) {
        hasntNullable = false
    }
    if (this.artworkUrl100 == null) {
        hasntNullable = false
    }
    if (this.collectionName == null) {
        hasntNullable = false
    }
    if (this.releaseDate == null) {
        hasntNullable = false
    }
    if (this.primaryGenreName == null) {
        hasntNullable = false
    }
    if (this.country == null) {
        hasntNullable = false
    }
    if (this.previewUrl == null) {
        hasntNullable = false
    }
    return hasntNullable
}