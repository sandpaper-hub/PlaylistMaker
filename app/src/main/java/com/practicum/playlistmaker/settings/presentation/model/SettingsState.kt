package com.practicum.playlistmaker.settings.presentation.model

sealed interface SettingsState {
    data class DarkTheme(val isChecked: Boolean): SettingsState
}