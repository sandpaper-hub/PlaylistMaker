package com.practicum.playlistmaker.application.domain.interactor

import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.application.presentation.interactor.DarkThemeInteractor

class DarkThemeInteractorImpl(private val themeRepository: AppThemeRepository) : DarkThemeInteractor{
    override fun getThemeValue(): Boolean {
        return themeRepository.getData()
    }

    override fun saveThemeValue(value: Boolean) {
        themeRepository.saveData(value)
    }
}