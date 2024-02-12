package com.practicum.playlistmaker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backButtonImageButton = findViewById<ImageButton>(R.id.back_button_playerActivity)
        val durationTextView = findViewById<TextView>(R.id.durationValue)
        val collectionTextView = findViewById<TextView>(R.id.albumValue)
        val yearTextView = findViewById<TextView>(R.id.yearValue)
        val genreTextView = findViewById<TextView>(R.id.genreValue)
        val countryTextView = findViewById<TextView>(R.id.countryValue)
        val albumCoverImageView = findViewById<ImageView>(R.id.album_cover)
        val trackNameTextView = findViewById<TextView>(R.id.trackNamePlayerActivity)
        val artistNameTextView = findViewById<TextView>(R.id.artistNamePlayerActivity)
        val collectionGroup = findViewById<Group>(R.id.collectionGroup)

        val playerIntent = intent
        val track = playerIntent.getParcelableExtra("selectedTrack", Track::class.java) ?: Track(
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
        durationTextView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackDuration)
        if (track.collectionName.isEmpty()) {
            collectionGroup.isVisible = false
        } else {
            collectionTextView.text = track.collectionName
        }
        yearTextView.text = track.releaseDate.take(4)
        genreTextView.text = track.primaryGenreName
        countryTextView.text = track.country
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        Glide.with(applicationContext)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .fitCenter()
            .placeholder(R.drawable.album)
            .transform(RoundedCorners(Transformer.dpToPx(2f, applicationContext)))
            .into(albumCoverImageView)

        backButtonImageButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}