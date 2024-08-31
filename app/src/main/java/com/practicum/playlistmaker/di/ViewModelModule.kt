package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylistFragment.CreatePlaylistViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.favorite.FavoriteTracksViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.MediaPlayerViewModel
import com.practicum.playlistmaker.search.presentation.TracksSearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    /* создание SettingsViewModel */
    viewModel<SettingsViewModel> {
        SettingsViewModel(get())
    }

    viewModel<MediaPlayerViewModel> {
        MediaPlayerViewModel(get())
    }

    viewModel<TracksSearchViewModel> {
        TracksSearchViewModel(get())
    }

    viewModel<FavoriteTracksViewModel> {
        FavoriteTracksViewModel(get())
    }

    viewModel<PlaylistsViewModel> { (message: String) ->
        PlaylistsViewModel(message)
    }

    viewModel<CreatePlaylistViewModel>{
        CreatePlaylistViewModel(get())
    }
}