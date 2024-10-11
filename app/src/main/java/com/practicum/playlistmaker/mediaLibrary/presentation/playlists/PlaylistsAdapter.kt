package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsViewHolder>() {
    val playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder.from(parent)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

}