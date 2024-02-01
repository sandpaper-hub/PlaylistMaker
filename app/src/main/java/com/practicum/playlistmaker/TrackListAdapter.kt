package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackListAdapter(var trackList: List<Track>, private val onTrackClickListener: OnTrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {
        interface OnTrackClickListener{
            fun onItemClick(track: Track)
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.track_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        val track = trackList[position]
        holder.itemView.setOnClickListener{
            onTrackClickListener.onItemClick(track)
        }
    }
}