package com.practicum.playlistmaker.domain.usecase.search

import com.practicum.playlistmaker.createArrayListFromJson
import com.practicum.playlistmaker.domain.repository.search.SharedPreferencesRepository
import com.practicum.playlistmaker.presentation.usecase.search.ClearHistoryUseCase

class ClearHistoryUseCaseImpl(private val sharedPreferencesRepository: SharedPreferencesRepository): ClearHistoryUseCase {
    override fun execute() {
        val json = sharedPreferencesRepository.getData()
        if (json != null) {
            val array = json.createArrayListFromJson()
            array.clear()
            sharedPreferencesRepository.save(array)
        }
    }
}