package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistsAdapter(private val onPlaylistListener: OnPlaylistListener) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {
    val playlists = ArrayList<Playlist>()

    interface OnPlaylistListener {
        fun onItemClick(playlist: Playlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder.from(parent)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        val playlist = playlists[position]
        holder.itemView.setOnClickListener {
            onPlaylistListener.onItemClick(playlist)
        }
    }

}