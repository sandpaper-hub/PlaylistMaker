package com.practicum.playlistmaker.player.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistsAdapterListView(private val onPlaylistListener: OnPlaylistListener) :
    RecyclerView.Adapter<PlaylistsViewHolderListView>() {

    val playlists = ArrayList<Playlist>()

    interface OnPlaylistListener{
        fun onItemClick(playlist: Playlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolderListView {
        return PlaylistsViewHolderListView.from(parent)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolderListView, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onPlaylistListener.onItemClick(playlists[position])
        }
    }

}