package com.practicum.playlistmaker.search.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.convertLongToTimeMillis
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.dpToPx

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val coverImageView: ImageView = itemView.findViewById(R.id.cover)
    private val trackNameTextView: TextView = itemView.findViewById(R.id.trackName)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.artistName)
    private val trackDurationTextView: TextView = itemView.findViewById(R.id.trackDuration)

    fun bind(track: Track) {
        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .fitCenter()
            .transform(RoundedCorners(2f.dpToPx(itemView.context)))
            .placeholder(R.drawable.placeholder)
            .into(coverImageView)
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        trackDurationTextView.text = track.trackDuration!!.convertLongToTimeMillis("mm:ss")
    }
}