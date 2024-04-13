package com.practicum.playlistmaker.search.domain.useCases

import com.practicum.playlistmaker.createArrayListFromJson
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SharedPreferencesRepository
import com.practicum.playlistmaker.search.presentation.GetHistoryUseCase

class GetHistoryUseCaseImpl(private val sharedPreferencesRepository: SharedPreferencesRepository) :
    GetHistoryUseCase {
    override fun execute(): ArrayList<Track> {
        val json = sharedPreferencesRepository.getData()
        return json?.createArrayListFromJson() ?: ArrayList()
    }
}