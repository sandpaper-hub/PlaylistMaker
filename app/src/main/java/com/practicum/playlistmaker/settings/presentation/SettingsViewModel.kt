package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.application.domain.api.DarkThemeInteractor
import com.practicum.playlistmaker.settings.presentation.model.SettingsState

class SettingsViewModel(private val darkThemeInteractor: DarkThemeInteractor) : ViewModel() {

    private val liveData = MutableLiveData<SettingsState>()
    fun observeState(): LiveData<SettingsState> {
        init()
        return liveData
    }

    private fun init() {
        renderState(SettingsState.DarkTheme(darkThemeInteractor.getThemeValue()))
    }

    fun switchTheme(isChecked: Boolean) {
        darkThemeInteractor.switchTheme(isChecked)
        darkThemeInteractor.saveThemeValue(isChecked)
        renderState(SettingsState.DarkTheme(isChecked))
    }

    private fun renderState(state: SettingsState) {
        liveData.postValue(state)
    }
}