package com.practicum.playlistmaker.playlist.presentation

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.util.declineTracksCount
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.getSerializableData

class PlaylistFragment : Fragment() {

    companion object {
        private const val PLAYLIST = "PLAYLIST"
        fun createArgs(playlist: Playlist): Bundle = bundleOf(PLAYLIST to playlist)
    }

    private var contextMenuImageViewBottomPoint: Int = 0
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlistBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var menuBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var playlist: Playlist
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

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

        menuBottomSheet = BottomSheetBehavior.from(binding.menuBottomSheetContainer)
        menuBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        playlistBottomSheet = BottomSheetBehavior.from(binding.tracksBottomSheetContainer)
        binding.contextMenuImageView.post {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            contextMenuImageViewBottomPoint = binding.contextMenuImageView.bottom
            playlistBottomSheet.peekHeight = screenHeight - contextMenuImageViewBottomPoint - 24f.dpToPx(requireContext())
        }
        binding.shareImageView.setOnClickListener {
           startShareIntent()
        }

        binding.contextMenuImageView.setOnClickListener {
            menuBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.shareTextView.setOnClickListener {
            startShareIntent()
        }

        binding.deleteTextView.setOnClickListener {
            confirmDialog.setMessage("${resources.getString(R.string.deletePlaylist)} «${playlist.playlistName}»")
            confirmDialog.show()
        }
    }

    private fun initialize() = with(binding) {
        albumNameTextView.text = playlist.playlistName
        albumDescriptionTextView.text = playlist.playlistDescription
        tracksCountTextView.text = playlist.tracksCount.declineTracksCount()
        Glide.with(requireContext())
            .load(playlist.playlistCover)
            .transform(CenterCrop())
            .placeholder(R.drawable.placeholder)
            .into(coverBigImageView)
        Glide.with(requireContext())
            .load(playlist.playlistCover)
            .transform(CenterCrop())
            .placeholder(R.drawable.placeholder)
            .into(coverSmallImageView)
        albumNameBottomSheetTextView.text = playlist.playlistName
        tracksCountBottomSheetTextView.text = playlist.tracksCount.declineTracksCount()
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setNegativeButton(R.string.no) { _, _ ->

            }
            .setPositiveButton(R.string.yes) { _, _ ->
                //TODO удаление трека из плейлиста
            }
    }

    private fun startShareIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            R.string.sampleMessageForShare
        )   //TODO передаваемый контент
        shareIntent.setType("text/plain")
        val intentChooser = Intent.createChooser(shareIntent, "")
        startActivity(intentChooser)
    }
}