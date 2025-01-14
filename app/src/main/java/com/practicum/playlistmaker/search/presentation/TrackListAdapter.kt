package com.practicum.playlistmaker.search.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class TrackListAdapter(
    private val onTrackClickListener: OnTrackClickListener,
    private val onTrackLongClickListener: OnTrackLongClickListener? = null
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var trackList = ArrayList<Track>()

    interface OnTrackClickListener {
        fun onItemClick(track: Track)
    }

    interface OnTrackLongClickListener {
        fun onItemLongClick(track: Track): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        val track = trackList[position]
        holder.itemView.setOnClickListener {
            onTrackClickListener.onItemClick(track)
        }

        holder.itemView.setOnLongClickListener {
            onTrackLongClickListener?.onItemLongClick(track) ?: false
        }
    }
}