package com.practicum.playlistmaker.data.repository

import android.content.Intent
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.TrackInfoRepository
import com.practicum.playlistmaker.getParcelableTrack
import com.practicum.playlistmaker.presentation.ui.SearchActivity

class TrackInfoRepositoryImpl(private val intent: Intent) : TrackInfoRepository {
    override fun getTrackInfo() : Track {
        return intent.getParcelableTrack<Track>(SearchActivity.INTENT_EXTRA_KEY) ?:
        Track("","","",
            0,"","","",
            "","","")
    }
}