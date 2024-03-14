package com.practicum.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()
    lateinit var mainHandler: Handler
    private lateinit var track: Track
    lateinit var binding: ActivityPlayerBinding

    //    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainHandler = Handler(Looper.getMainLooper())

        track = intent.getParcelableTrack<Track>(SearchActivity.INTENT_EXTRA_KEY) ?: Track(
            "", "", "", 0,
            "", "", "",
            "", "", ""
        )
        preparePlayer()
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
            playBackControl()
            if (playerState == STATE_PLAYING) {
                mainHandler.post(timerRunnableObject)
            } else {
                mainHandler.removeCallbacks(timerRunnableObject)
            }
        }
    }

    private val timerRunnableObject = object : Runnable {
        override fun run() {
            binding.durationCurrentValue.text =
                mediaPlayer.currentPosition.convertLongToTimeMillis()
            mainHandler.postDelayed(this, 300)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainHandler.removeCallbacks(timerRunnableObject)
    }

    private fun playBackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PAUSED, STATE_PREPARED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playButton.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            mainHandler.removeCallbacks(timerRunnableObject)
            binding.playButton.setImageResource(R.drawable.play_button)
            binding.durationCurrentValue.setText(R.string.durationSample)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }
}