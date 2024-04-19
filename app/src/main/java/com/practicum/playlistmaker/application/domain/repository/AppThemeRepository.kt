package com.practicum.playlistmaker.application.domain.repository

interface AppThemeRepository {
    fun getData(): Boolean
    fun saveData(value: Boolean)
}