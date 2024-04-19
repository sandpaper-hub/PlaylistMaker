package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.util.GlobalConstants
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.convertLongToTimeMillis
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.getParcelableTrack
import com.practicum.playlistmaker.player.domain.model.MediaPlayerState
import com.practicum.playlistmaker.player.presentation.MediaPlayerViewModel
import com.practicum.playlistmaker.player.ui.model.PlayerState

class PlayerActivity : AppCompatActivity() {

    private var playerState = MediaPlayerState.STATE_DEFAULT

    private lateinit var track: Track
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var mediaPlayerViewModel: MediaPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayerViewModel = ViewModelProvider(
            this,
            MediaPlayerViewModel.getViewModelFactory()
        )[MediaPlayerViewModel::class.java]

        mediaPlayerViewModel.observeState().observe(this) { render(it) }


        track = intent.getParcelableTrack<Track>(GlobalConstants.INTENT_EXTRA_KEY) ?: Track(
            "", "", "",
            0, "", "", "",
            "", "", ""
        )
        playerState = mediaPlayerViewModel.preparePlayer(track.previewUrl)

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
        Glide.with(applicationContext)
            .load(track.artworkUrl100!!.replaceAfterLast('/', "512x512bb.jpg"))
            .fitCenter()
            .placeholder(R.drawable.album)
            .transform(RoundedCorners(2f.dpToPx(applicationContext)))
            .into(binding.albumCover)

        binding.backButtonPlayerActivity.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.playButton.setOnClickListener {
            playerState = mediaPlayerViewModel.playbackControl(playerState)
        }
    }

    override fun onPause() {
        super.onPause()
        playerState = mediaPlayerViewModel.playbackControl(MediaPlayerState.STATE_PLAYING)
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerViewModel.releaseMediaPlayer()
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.ChangePosition -> onTrackPositionChanged(state.position)
            is PlayerState.Prepared -> onPreparedPlayer()
            is PlayerState.Playing -> onPlayerStart()
            is PlayerState.Pause -> onPlayerPaused()
            is PlayerState.Complete -> onTrackComplete()
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

    private fun onPreparedPlayer() {
        binding.playButton.isEnabled = true
    }

    private fun onTrackComplete() {
        playerState = MediaPlayerState.STATE_PREPARED
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.durationCurrentValue.setText(R.string.durationSample)
    }
}