package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val coverImageView: ImageView = itemView.findViewById(R.id.cover)
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistName)
    private val trackDurationTextView: TextView = itemView.findViewById(R.id.trackDuration)

    fun bind(model: Track) {
        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.placeholder)
            .into(coverImageView)
        trackNameTextView.text = model.trackName
        artistNameTextView.text = model.artistName
        trackDurationTextView.text = model.trackDuration
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}