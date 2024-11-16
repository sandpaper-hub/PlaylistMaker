package com.practicum.playlistmaker.mediaLibrary.presentation.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistGridViewBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.util.reformatCount
import com.practicum.playlistmaker.util.dpToPx

class PlaylistsViewHolder(private val binding: PlaylistGridViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        Glide.with(binding.root.context)
            .load(playlist.playlistCover)
            .transform(CenterCrop(), RoundedCorners(8f.dpToPx(binding.root.context)))
            .placeholder(R.drawable.placeholder)
            .into(binding.albumCoverImageView)
        binding.albumNameTextView.text = playlist.playlistName
        binding.albumCountTextView.text =
            playlist.tracksCount.reformatCount("трек", "трека", "треков")
    }

    companion object {
        fun from(parent: ViewGroup): PlaylistsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PlaylistGridViewBinding.inflate(layoutInflater, parent, false)
            return PlaylistsViewHolder(binding)
        }
    }
}