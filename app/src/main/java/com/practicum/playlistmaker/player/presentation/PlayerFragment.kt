package com.practicum.playlistmaker.player.presentation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
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
    private val mediaPlayerViewModel by viewModel<MediaPlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaPlayerViewModel.observeState().observe(viewLifecycleOwner) { render(it) }

        track = requireArguments().getSerializableTrack<Track>(TRACK)!!
        mediaPlayerViewModel.createPlayer()
        mediaPlayerViewModel.preparePlayer(track.previewUrl)
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        mediaPlayerViewModel.pausePlayer()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerViewModel.releaseMediaPlayer()
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Created -> onPlayerCreate()
            is PlayerState.Prepared -> onPlayerPrepared()
            is PlayerState.Playing -> onPlayerStart()
            is PlayerState.Pause -> onPlayerPaused()
            is PlayerState.Complete -> onTrackComplete()
            is PlayerState.ChangePosition -> onTrackPositionChanged(state.position)
        }
    }

    private fun onPlayerCreate() {
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
        Glide.with(requireContext())
            .load(track.artworkUrl100!!.replaceAfterLast('/', "512x512bb.jpg"))
            .fitCenter()
            .placeholder(R.drawable.album)
            .transform(RoundedCorners(2f.dpToPx(requireContext())))
            .into(binding.albumCover)

        binding.backButtonPlayerActivity.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onPlayerPrepared() {
        binding.playButton.isClickable = true
        binding.playButton.setOnClickListener {
            mediaPlayerViewModel.playbackControl()
        }
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