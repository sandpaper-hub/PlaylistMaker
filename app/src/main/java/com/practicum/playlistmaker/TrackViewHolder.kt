package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val coverImageView: ImageView = itemView.findViewById(R.id.cover)
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistName)
    private val trackDurationTextView: TextView = itemView.findViewById(R.id.trackDuration)

    fun bind(track: Track) {
        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .transform(RoundedCorners(Transformer.dpToPx(2f, itemView.context)))
            .fitCenter()
            .placeholder(R.drawable.placeholder)
            .into(coverImageView)
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackDurationTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackDuration)
    }
}