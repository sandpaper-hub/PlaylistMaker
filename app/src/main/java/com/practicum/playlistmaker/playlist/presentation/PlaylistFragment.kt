package com.practicum.playlistmaker.playlist.presentation

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.playlist.presentation.model.PlaylistState
import com.practicum.playlistmaker.util.reformatCount
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.setImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        private const val PLAYLIST = "PLAYLIST"
        private const val POSITIVE_BUTTON_TEXT = "Да"
        private const val NEGATIVE_BUTTON_TEXT = "Нет"
        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST to playlistId)
    }

    private var contextMenuImageViewBottomPoint: Int = 0
    private val viewModel by viewModel<PlaylistViewModel>()
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlistBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var menuBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private val confirmDialog = MaterialAlertDialogBuilder(requireContext())

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
        viewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }
        menuBottomSheet = BottomSheetBehavior.from(binding.menuBottomSheetContainer)
        menuBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        viewModel.initialize(requireArguments().getInt(PLAYLIST))
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Initialized -> initialize(state.playlist, state.totalTime)
        }
    }

    private fun initialize(playlist: Playlist, totalTime: String) {
        setPlaylistInfo(playlist, totalTime)
        setBottomSheet()
        setOnClickListeners(playlist)
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

    private fun setPlaylistInfo(playlist: Playlist, totalTime: String) = with(binding) {
        albumNameTextView.text = playlist.playlistName
        albumDescriptionTextView.text = playlist.playlistDescription
        albumTotalTimeTextView.text = totalTime
        tracksCountTextView.text = playlist.tracksCount.reformatCount("трек", "трека", "треков")
        albumNameBottomSheetTextView.text = playlist.playlistName
        tracksCountBottomSheetTextView.text = playlist.tracksCount.reformatCount("трек", "трека", "треков")
        coverBigImageView.setImage(requireContext(), playlist.playlistCover)
        coverSmallImageView.setImage(requireContext(), playlist.playlistCover)
    }

    private fun setBottomSheet() {
        playlistBottomSheet = BottomSheetBehavior.from(binding.tracksBottomSheetContainer)
        binding.contextMenuImageView.post {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            contextMenuImageViewBottomPoint = binding.contextMenuImageView.bottom
            playlistBottomSheet.peekHeight =
                screenHeight - contextMenuImageViewBottomPoint - 24f.dpToPx(requireContext())
        }
    }

    private fun setOnClickListeners(playlist: Playlist) = with(binding) {
        panelHeader.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        shareImageView.setOnClickListener {
            startShareIntent()
        }

        contextMenuImageView.setOnClickListener {
            menuBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        shareTextView.setOnClickListener {
            startShareIntent()
        }

        deleteTextView.setOnClickListener {
            confirmDialog.setMessage("${resources.getString(R.string.deletePlaylist)} «${playlist.playlistName}»")
            confirmDialog.show()
        }
    }
}