package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.TypedValue
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

inline fun <reified T : Parcelable> Intent.getParcelableTrack(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
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