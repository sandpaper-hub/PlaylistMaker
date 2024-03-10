package com.practicum.playlistmaker

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
inline fun <reified T : Parcelable> Intent.getParcelableTrack(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T

}