package com.practicum.playlistmaker.playlist.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.editPlaylist.presentation.EditPlaylistFragment
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.player.presentation.PlayerFragment
import com.practicum.playlistmaker.playlist.presentation.model.PlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.presentation.TrackListAdapter
import com.practicum.playlistmaker.util.clickDebounce
import com.practicum.playlistmaker.util.convertLongToTimeMillis
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
        menuBottomSheet.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_DRAGGING, BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.dimView.visibility = View.VISIBLE
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            binding.dimView.alpha = 1f
                        }
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.dimView.visibility = View.GONE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= 0.0f) {
                    binding.dimView.alpha = slideOffset + 1f
                }
            }
        })
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
                viewModel.showDeleteTrackDialog(track.trackId.toString())
                return true
            }

        })
        binding.playlistRecyclerView.adapter = trackListAdapter
        Log.d("EXAMPLE_TEST", "init")
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
            is PlaylistState.ShareIntent -> startShareIntent(state.tracks, state.playlist)
            is PlaylistState.DialogTrackDelete -> createDialog(state.message) {
                viewModel.deleteTrack(state.objectId)
            }.show()

            is PlaylistState.DialogPlaylistDelete -> showDeletePlaylistDialog(
                state.message
            )

            is PlaylistState.PlaylistDeleted -> findNavController().navigateUp()
        }
    }

    private fun initialize(playlist: Playlist, totalTime: String, tracks: List<Track>) {
        setPlaylistInfo(playlist, totalTime)
        setBottomSheet()
        setRecyclerViewData(tracks)
        setListeners(playlist)
        if (tracks.isEmpty()) {
            showToast(resources.getString(R.string.noTracksForSharing))
        }
    }

    private fun startShareIntent(tracksList: List<Track>, playlist: Playlist) {
        if (tracksList.isEmpty()) {
            showToast(resources.getString(R.string.emptyPlaylist))
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                tracksList.formatToStringSharing(playlist)
            )
            shareIntent.setType("text/plain")
            val intentChooser = Intent.createChooser(shareIntent, "")
            startActivity(intentChooser)
        }
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
            viewModel.shareIntent()
        }

        contextMenuImageView.setOnClickListener {
            menuBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        shareTextView.setOnClickListener {
            viewModel.shareIntent()
        }

        deleteTextView.setOnClickListener {
            viewModel.showDeletePlaylistDialog()
        }

        editInfoTextView.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlist)
            )
        }
    }

    private fun showDeletePlaylistDialog(
        message: String
    ) {
        menuBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        createDialog(message) {
            viewModel.deletePlaylist()
        }.show()
    }

    private fun createDialog(
        message: String,
        action: () -> Unit
    ): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                action()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecyclerViewData(tracks: List<Track>) {
        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun updatePlaylist(tracks: List<Track>, totalTime: String) {
        setRecyclerViewData(tracks)
        binding.tracksCountTextView.text = tracks.size.reformatCount("трек", "трека", "треков")
        binding.tracksCountBottomSheetTextView.text =
            tracks.size.reformatCount("трек", "трека", "треков")
        binding.albumTotalTimeTextView.text = totalTime
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

fun List<Track>.formatToStringSharing(playlist: Playlist): String {
    val sharingString =
        StringBuilder().append(
            "${playlist.playlistName}\n" +
                    "${playlist.playlistDescription}\n" +
                    this.size.reformatCount("трек", "трека", "треков")
        )
    this.forEachIndexed { index, track ->
        val artist = track.artistName ?: "Неизвестный исполнитель"
        val name = track.trackName ?: "Неизвестный трек"
        val duration = track.trackDuration!!.convertLongToTimeMillis("mm:ss")
        sharingString.append("${index + 1}. $artist - $name ($duration)\n")
    }
    return sharingString.toString()
}