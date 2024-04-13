package com.practicum.playlistmaker.search.domain.useCases

import com.practicum.playlistmaker.createArrayListFromJson
import com.practicum.playlistmaker.search.domain.repository.SharedPreferencesRepository
import com.practicum.playlistmaker.search.presentation.ClearHistoryUseCase

class ClearHistoryUseCaseImpl(private val sharedPreferencesRepository: SharedPreferencesRepository):
    ClearHistoryUseCase {
    override fun execute() {
        val json = sharedPreferencesRepository.getData()
        if (json != null) {
            val array = json.createArrayListFromJson()
            array.clear()
            sharedPreferencesRepository.save(array)
        }
    }
}