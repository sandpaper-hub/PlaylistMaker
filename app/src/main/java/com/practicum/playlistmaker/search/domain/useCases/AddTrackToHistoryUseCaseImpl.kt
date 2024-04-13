package com.practicum.playlistmaker.search.domain.useCases

import com.practicum.playlistmaker.createArrayListFromJson
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SharedPreferencesRepository
import com.practicum.playlistmaker.search.presentation.TrackToHistoryUseCase

class AddTrackToHistoryUseCaseImpl(private val sharedPreferencesRepository: SharedPreferencesRepository):
    TrackToHistoryUseCase {
    override fun execute(track: Track) {
        val json = sharedPreferencesRepository.getData()
        val historyArray = json?.createArrayListFromJson() ?: ArrayList()
        historyArray.removeIf { it == track }
        historyArray.add(0, track)
        if (historyArray.size > 10) {
            historyArray.removeAt(10)
        }
        sharedPreferencesRepository.save(historyArray)
    }
}