package com.practicum.playlistmaker.application.domain.api

interface DarkThemeInteractor {
    fun getThemeValue(): Boolean
    fun saveThemeValue(value: Boolean)
}