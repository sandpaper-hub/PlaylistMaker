package com.practicum.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.ui.SettingsState

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(this[APPLICATION_KEY] as Application) }
        }

    }

    private val darkThemeInteractor = Creator.provideDarkThemeInteractor(getApplication())

    private val stateLiveData = MutableLiveData<SettingsState>()
    fun observeState(): LiveData<SettingsState> = stateLiveData

    fun isChecked(): Boolean {
        val isChecked = darkThemeInteractor.getThemeValue()
        renderState(SettingsState.Switcher(isChecked))
        return isChecked
    }

    private fun renderState(state: SettingsState) {
        stateLiveData.postValue(state)
    }
}