package com.practicum.playlistmaker.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.convertLongToTimeMillis
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.presentation.usecase.PlaybackControlUseCase
import com.practicum.playlistmaker.presentation.usecase.UpdateTrackTimerUseCase
import com.practicum.playlistmaker.dpToPx
import com.practicum.playlistmaker.presentation.usecase.ReleasePlayerUseCase

class PlayerActivity : AppCompatActivity(), MediaPlayerListener {

    private var playerState = MediaPlayerState.STATE_DEFAULT
    private lateinit var playbackControlUseCase: PlaybackControlUseCase
    private lateinit var updateTrackTimerUseCase: UpdateTrackTimerUseCase
    private lateinit var releasePlayerUseCase: ReleasePlayerUseCase
    private lateinit var track: Track
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getTrackInfoUseCase = Creator.getTrackInfoUseCase(intent)
        track = getTrackInfoUseCase.execute()
        val preparePlayerUseCase = Creator.getPreparePlayerUseCase(this, track.previewUrl)
        playerState = preparePlayerUseCase.execute()
        playbackControlUseCase = Creator.getPlaybackControlUseCase()
        updateTrackTimerUseCase = Creator.getUpdateTrackTimerUseCase(this)
        releasePlayerUseCase = Creator.getReleasePlayerUseCase()

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
            playerState = playbackControlUseCase.execute(playerState)
            updateTrackTimerUseCase.execute(playerState)
        }
    }

    override fun onPlayerStart() {
        binding.playButton.setImageResource(R.drawable.pause_button)
    }

    override fun onPlayerPaused() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    override fun onPause() {
        super.onPause()
        playerState = playbackControlUseCase.execute(MediaPlayerState.STATE_PLAYING)
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerState = MediaPlayerState.STATE_PREPARED
        releasePlayerUseCase.execute()
        updateTrackTimerUseCase.execute(playerState)
    }

    override fun onTrackPositionChanged(position: String) {
        binding.durationCurrentValue.text = position
    }

    override fun onPreparedPlayer() {
        binding.playButton.isEnabled = true
    }

    override fun onTrackComplete() {
        playerState = MediaPlayerState.STATE_PREPARED
        updateTrackTimerUseCase.execute(playerState)
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.durationCurrentValue.setText(R.string.durationSample)
    }
}