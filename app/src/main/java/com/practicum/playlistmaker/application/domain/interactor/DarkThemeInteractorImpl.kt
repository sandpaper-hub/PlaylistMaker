package com.practicum.playlistmaker.application.domain.interactor

import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.application.domain.repository.AppThemeRepository

class DarkThemeInteractorImpl(private val themeRepository: AppThemeRepository) :
    DarkThemeInteractor {
    override fun getThemeValue(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun saveThemeValue(value: Boolean) {
        themeRepository.setDarkThemeEnabled(value)
    }
}