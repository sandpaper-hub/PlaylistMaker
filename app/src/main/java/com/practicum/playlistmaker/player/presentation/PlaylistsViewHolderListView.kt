package com.practicum.playlistmaker.player.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistLinearViewBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.util.declineTracksCount
import com.practicum.playlistmaker.util.dpToPx

class PlaylistsViewHolderListView(private val playlistLinearViewBinding: PlaylistLinearViewBinding) :
    RecyclerView.ViewHolder(playlistLinearViewBinding.root) {
    fun bind(playlist: Playlist) {
        Glide.with(playlistLinearViewBinding.root.context)
            .load(playlist.playlistCover)
            .transform(
                CenterCrop(),
                RoundedCorners(2f.dpToPx(playlistLinearViewBinding.root.context))
            )
            .placeholder(R.drawable.placeholder)
            .into(playlistLinearViewBinding.playlistCoverImageView)
        playlistLinearViewBinding.playlistNameTextView.text = playlist.playlistName
        playlistLinearViewBinding.tracksCountTextView.text = playlist.tracksCount.declineTracksCount()
    }

    companion object {
        fun from(parent: ViewGroup): PlaylistsViewHolderListView {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PlaylistLinearViewBinding.inflate(layoutInflater, parent, false)
            return PlaylistsViewHolderListView(binding)
        }
    }
}