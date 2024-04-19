package com.practicum.playlistmaker.application.presentation.interactor

interface DarkThemeInteractor {
    fun getThemeValue(): Boolean
    fun saveThemeValue(value: Boolean)
}