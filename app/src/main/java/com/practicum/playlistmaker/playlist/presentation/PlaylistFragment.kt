package com.practicum.playlistmaker.playlist.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.presentation.PlayerFragment
import com.practicum.playlistmaker.playlist.presentation.model.PlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.TrackListAdapter
import com.practicum.playlistmaker.util.clickDebounce
import com.practicum.playlistmaker.util.reformatCount
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.setImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        private const val PLAYLIST = "PLAYLIST"
        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST to playlistId)
    }

    private var contextMenuImageViewBottomPoint: Int = 0
    private var isClickAllowed = true
    private val viewModel by viewModel<PlaylistViewModel>()
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var playlistBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private lateinit var menuBottomSheet: BottomSheetBehavior<ConstraintLayout>
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
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        menuBottomSheet = BottomSheetBehavior.from(binding.menuBottomSheetContainer)
        menuBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        confirmDialog =
            MaterialAlertDialogBuilder(requireContext()).setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
        trackListAdapter = TrackListAdapter(object : TrackListAdapter.OnTrackClickListener {
            override fun onItemClick(track: Track) {
                if (clickDebounce({ isClickAllowed }, { newValue -> isClickAllowed = newValue })) {
                    findNavController().navigate(
                        R.id.action_playlistFragment_to_playerFragment,
                        PlayerFragment.createArgs(track)
                    )
                }
            }
        }, object : TrackListAdapter.OnTrackLongClickListener {
            override fun onItemLongClick(track: Track): Boolean {
                confirmDialog.setMessage(resources.getString(R.string.deleteTrack))
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                        viewModel.deleteTrack(track.trackId.toString())
                    }.show()
                return true
            }

        })
        binding.playlistRecyclerView.adapter = trackListAdapter
        viewModel.initialize(requireArguments().getInt(PLAYLIST))
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Initialized -> initialize(
                state.playlist,
                state.totalTime,
                state.tracks
            )

            is PlaylistState.Updated -> updatePlaylist(state.tracks, state.totalTime)
        }
    }

    private fun initialize(playlist: Playlist, totalTime: String, tracks: List<Track>) {
        setPlaylistInfo(playlist, totalTime)
        setBottomSheet()
        setRecyclerViewData(tracks)
        setListeners(playlist)
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
        tracksCountBottomSheetTextView.text =
            playlist.tracksCount.reformatCount("трек", "трека", "треков")
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

    private fun setListeners(playlist: Playlist) = with(binding) {
        panelHeader.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }

            })

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
            confirmDialog.setMessage(
                "${resources.getString(R.string.deletePlaylist)} «${playlist.playlistName}»"
            )
            confirmDialog.show()
        }
    }

    private fun updatePlaylist(tracks: List<Track>, totalTime: String) {
        val count = tracks.count()
        setRecyclerViewData(tracks)
        binding.tracksCountTextView.text = count.reformatCount("трек", "трека", "треков")
        binding.albumTotalTimeTextView.text = totalTime
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewData(tracks: List<Track>) {
        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }
}