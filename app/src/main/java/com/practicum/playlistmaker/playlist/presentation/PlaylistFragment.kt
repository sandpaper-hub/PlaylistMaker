package com.practicum.playlistmaker.playlist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.util.declineTracksCount
import com.practicum.playlistmaker.util.getSerializableData

class PlaylistFragment : Fragment() {

    companion object {
        private const val PLAYLIST = "PLAYLIST"
        fun createArgs(playlist: Playlist): Bundle = bundleOf(PLAYLIST to playlist)
    }

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var playlist: Playlist

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlist = requireArguments().getSerializableData<Playlist>(PLAYLIST)!!
        initialize()
        binding.panelHeader.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
//        bottomSheet = BottomSheetBehavior.from(binding.bottomSheetContainer) TODO bottomSheet
//        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
//        val bottomSheetAdaptivePeekHeight = (screenHeight * 0.25).toInt()
//        bottomSheet.peekHeight = bottomSheetAdaptivePeekHeight
    }

    private fun initialize() = with(binding) {
        albumNameTextView.text = playlist.playlistName
        albumDescriptionTextView.text = playlist.playlistDescription
        tracksCountTextView.text = playlist.tracksCount.declineTracksCount()
        Glide.with(requireContext())
            .load(playlist.playlistCover)
            .transform(CenterCrop())
            .placeholder(R.drawable.placeholder)
            .into(coverImageView)
    }
}