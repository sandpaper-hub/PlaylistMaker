package com.practicum.playlistmaker.settings.ui

sealed interface SettingsState {
    data class Switcher(val checked: Boolean): SettingsState
}