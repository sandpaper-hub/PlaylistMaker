package com.practicum.playlistmaker.player.presentation

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistsAdapterListView : RecyclerView.Adapter<PlaylistsViewHolderListView>() {
    val playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolderListView {
        return PlaylistsViewHolderListView.from(parent)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolderListView, position: Int) {
        holder.bind(playlists[position])
    }

}