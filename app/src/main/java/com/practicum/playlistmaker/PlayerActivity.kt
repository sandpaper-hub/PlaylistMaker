package com.practicum.playlistmaker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerIntent = intent
        val track = playerIntent.getParcelableExtra(SearchActivity.INTENT_EXTRA_KEY, Track::class.java) ?: Track(
            "",
            "",
            "",
            0,
            "",
            "",
            "",
            "",
            ""
        )
        binding.durationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackDuration)
        if (track.collectionName.isEmpty()) {
            binding.collectionGroup.isVisible = false
        } else {
            binding.albumValue.text = track.collectionName
        }
        binding.yearValue.text = track.releaseDate.take(4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.trackNamePlayerActivity.text = track.trackName
        binding.artistNamePlayerActivity.text = track.artistName
        Glide.with(applicationContext)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .fitCenter()
            .placeholder(R.drawable.album)
            .transform(RoundedCorners(Transformer.dpToPx(2f, applicationContext)))
            .into(binding.albumCover)

        binding.backButtonPlayerActivity.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}