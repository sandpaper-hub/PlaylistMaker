package com.practicum.playlistmaker.presentation.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.MediaPlayerState
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.convertLongToTimeMillis
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.TrackInfoRepositoryImpl
import com.practicum.playlistmaker.data.repository.UpdateTrackTimerRepositoryImpl
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.TrackPositionListener
import com.practicum.playlistmaker.domain.usecases.GetTrackInfoUseCase
import com.practicum.playlistmaker.domain.usecases.PlaybackControlUseCase
import com.practicum.playlistmaker.domain.usecases.PreparePlayerUseCase
import com.practicum.playlistmaker.domain.usecases.UpdateTrackTimerUseCase
import com.practicum.playlistmaker.dpToPx

class PlayerActivity : AppCompatActivity(), TrackPositionListener {

    private var playerState = MediaPlayerState.STATE_DEFAULT
    private lateinit var playbackControlUseCase: PlaybackControlUseCase
    private lateinit var updateTrackTimerUseCase: UpdateTrackTimerUseCase
    private val mediaPlayer = MediaPlayer()
    private lateinit var mainHandler: Handler
    private lateinit var track: Track
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackInfoRepositoryImpl = TrackInfoRepositoryImpl(intent)
        val getTrackInfoUseCase = GetTrackInfoUseCase(trackInfoRepositoryImpl)
        track = getTrackInfoUseCase.execute()
        val playerRepositoryImpl = PlayerRepositoryImpl(mediaPlayer, track.previewUrl)
        val preparePlayerUseCase = PreparePlayerUseCase(playerRepositoryImpl)
        playbackControlUseCase = PlaybackControlUseCase(playerRepositoryImpl)
        playerState = preparePlayerUseCase.execute()
        mainHandler = Handler(Looper.getMainLooper())
        val updateTrackTimerRepositoryImpl =
            UpdateTrackTimerRepositoryImpl(this, mainHandler, mediaPlayer)
        updateTrackTimerUseCase = UpdateTrackTimerUseCase(updateTrackTimerRepositoryImpl)

        mediaPlayer.setOnPreparedListener {
            binding.playButton.isEnabled = true
        }

        mediaPlayer.setOnCompletionListener {
            playerState = MediaPlayerState.STATE_PREPARED
            updateTrackTimerUseCase.execute(playerState)
            binding.playButton.setImageResource(R.drawable.play_button)
            binding.durationCurrentValue.setText(R.string.durationSample)
        }

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
            when (playerState) {
                MediaPlayerState.STATE_PLAYING -> {
                    binding.playButton.setImageResource(R.drawable.pause_button)
                }

                else -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        playerState = playbackControlUseCase.execute(MediaPlayerState.STATE_PLAYING)
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerState = MediaPlayerState.STATE_PREPARED
        mediaPlayer.release()
        updateTrackTimerUseCase.execute(playerState)
    }

    override fun onTrackPositionChanged(position: Int) {
        val positionInLong = position.toLong()
        binding.durationCurrentValue.text = positionInLong.convertLongToTimeMillis()
    }
}