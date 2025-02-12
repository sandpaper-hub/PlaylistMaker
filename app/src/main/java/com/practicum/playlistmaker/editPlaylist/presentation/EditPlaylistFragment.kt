package com.practicum.playlistmaker.editPlaylist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylist.CreatePlaylistFragment
import com.practicum.playlistmaker.mediaLibrary.presentation.model.CreatePlaylistState
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.getSerializableData
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    companion object {
        private const val PLAYLIST = "PLAYLIST"
        fun createArgs(playlist: Playlist): Bundle = bundleOf(PLAYLIST to playlist)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.initialize(requireArguments().getSerializableData<Playlist>(PLAYLIST)!!)
    }

    override fun render(state: CreatePlaylistState) {
        when (state) {
            is CreatePlaylistState.Initialized -> initialize(state.playlist)
            is CreatePlaylistState.EnableCreateButton -> enableCreateButton(state.isEnable)
        }
    }

    private fun initialize(playlist: Playlist) {
        setPlaylistInfo(playlist)
        setListeners(playlist)
    }

    private fun setPlaylistInfo(playlist: Playlist) {
        binding.panelHeader.text = resources.getString(R.string.editPlaylist)
        binding.createButton.text = resources.getString(R.string.save)
        binding.albumNameEditText.setText(playlist.playlistName)
        binding.albumDescriptionEditText.setText(playlist.playlistDescription)
        Glide.with(requireContext())
            .load(playlist.playlistCover)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(8f.dpToPx(requireContext())))
            .into(binding.albumCoverImageView)
        binding.albumCoverImageView.background = null
    }

    private fun setListeners(playlist: Playlist) {
        binding.createButton.setOnClickListener {
            viewModel.savePlaylist(
                coverUriString,
                "${binding.albumNameEditText.text.toString()}_${System.currentTimeMillis()}.jpg",
                Playlist(
                    playlist.id,
                    binding.albumNameEditText.text.toString(),
                    binding.albumDescriptionEditText.text.toString(),
                    playlist.playlistCover,
                    playlist.tracksId,
                    playlist.tracksCount
                )
            )
            findNavController().navigateUp()
        }

        binding.backButtonFragmentCreate.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }
    }
}