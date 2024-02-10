package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import com.google.gson.Gson

class Transformer {

    companion object {
        fun createJsonFromArrayList(arrayList: ArrayList<Track>): String {
            return Gson().toJson(arrayList)
        }

        fun createArrayListFromJson(json: String): ArrayList<Track> {
            val array = Gson().fromJson(json, Array<Track>::class.java)
            return ArrayList(array.toList())
        }

        fun createTrackFromJson(json: String): Track {
            return Gson().fromJson(json, Track::class.java)
        }

        fun createJsonFromTrack(track: Track): String {
            return Gson().toJson(track)
        }

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}
