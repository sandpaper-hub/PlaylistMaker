package com.practicum.playlistmaker.player.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.mediaLibrary.presentation.model.PlaylistsState
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.model.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.convertLongToTimeMillis
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.getSerializableTrack
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    companion object {
        private const val TRACK = "track"
        fun createArgs(track: Track): Bundle = bundleOf(TRACK to track)
    }

    private lateinit var track: Track
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val mediaPlayerViewModel by viewModel<MediaPlayerViewModel>()
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private lateinit var playlistsAdapter: PlaylistsAdapterListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButtonPlayerActivity.setOnClickListener {
            findNavController().navigateUp()
        }
        mediaPlayerViewModel.observeState().observe(viewLifecycleOwner) { renderPlayer(it) }
        playlistsViewModel.observeState().observe(viewLifecycleOwner) {
            renderBottomSheet(it)
        }

        playlistsViewModel.fillData()

        track = requireArguments().getSerializableTrack<Track>(TRACK)!!
        mediaPlayerViewModel.createPlayer(track.trackId)
        mediaPlayerViewModel.preparePlayer(track.previewUrl)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.addToCollectionButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_DRAGGING, BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.dimView.visibility = View.VISIBLE
                        playlistsViewModel.fillData()
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

        playlistsAdapter =
            PlaylistsAdapterListView(object : PlaylistsAdapterListView.OnPlaylistListener {
                override fun onItemClick(playlist: Playlist) {
                    playlistsViewModel.updatePlaylist(playlist, track)
                }
            })
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
        }

        binding.playButton.setOnClickListener {
            mediaPlayerViewModel.playbackControl()
        }
        binding.favoriteButton.setOnClickListener {
            mediaPlayerViewModel.updateFavorite(track)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayerViewModel.pausePlayer()
    }

    private fun renderPlayer(state: PlayerState) {
        when (state) {
            is PlayerState.Created -> onPlayerCreate(state.inFavorite)
            is PlayerState.Prepared -> onPlayerPrepared(state.position)
            is PlayerState.Playing -> onPlayerStart()
            is PlayerState.Pause -> onPlayerPaused()
            is PlayerState.Complete -> onTrackComplete()
            is PlayerState.ChangePosition -> onTrackPositionChanged(state.position)
            is PlayerState.Favorite -> updateFavorite(state.inFavorite)
        }
    }

    private fun renderBottomSheet(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.AddingResult -> showSuccessful(state.playlist, state.result)
        }
    }

    private fun showSuccessful(playlist: Playlist, result: Boolean) {
        if (result) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            Toast.makeText(
                requireContext(),
                "${getString(R.string.successfullyAdd)} ${playlist.playlistName}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "${getString(R.string.notAddedToPlaylist)} ${playlist.playlistName}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun showEmpty() {
        binding.emptyLibraryImageView.visibility = View.VISIBLE
        binding.emptyFavoriteTextView.visibility = View.VISIBLE
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.emptyFavoriteTextView.setText(R.string.noPlaylist)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(playlists: List<Playlist>) {
        binding.emptyLibraryImageView.visibility = View.GONE
        binding.emptyFavoriteTextView.visibility = View.GONE
        binding.playlistsRecyclerView.visibility = View.VISIBLE
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun updateFavorite(inFavorite: Boolean) {
        if (inFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.infavorite_button)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.non_favorite_button)
        }
    }

    private fun onPlayerCreate(inFavorite: Boolean) {
        binding.durationValue.text =
            track.trackDuration!!.convertLongToTimeMillis()
        if (track.collectionName!!.isEmpty()) {
            binding.collectionGroup.isVisible = track.collectionName!!.isNotEmpty()
        } else {
            binding.albumValue.text = track.collectionName
        }
        binding.yearValue.text = track.releaseDate!!.take(4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.trackNamePlayerActivity.text = track.trackName
        binding.artistNamePlayerActivity.text = track.artistName
        updateFavorite(inFavorite)

        Glide.with(requireContext())
            .load(track.artworkUrl100!!.replaceAfterLast('/', "512x512bb.jpg"))
            .fitCenter()
            .placeholder(R.drawable.album)
            .transform(RoundedCorners(8f.dpToPx(requireContext())))
            .into(binding.albumCover)

        binding.backButtonPlayerActivity.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onPlayerPrepared(position: String) {
        binding.playButton.isClickable = true
        binding.durationCurrentValue.text = position
    }

    private fun onPlayerStart() {
        binding.playButton.setImageResource(R.drawable.pause_button)
    }

    private fun onPlayerPaused() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    private fun onTrackPositionChanged(position: String) {
        binding.durationCurrentValue.text = position
    }

    private fun onTrackComplete() {
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.durationCurrentValue.setText(R.string.durationSample)
    }
}