package com.practicum.playlistmaker.application.domain.repository

interface AppThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(value: Boolean)
}