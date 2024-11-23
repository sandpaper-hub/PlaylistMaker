package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediaLibrary.presentation.createPlaylist.CreatePlaylistViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.favoriteTracks.FavoriteTracksViewModel
import com.practicum.playlistmaker.mediaLibrary.presentation.playlists.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.MediaPlayerViewModel
import com.practicum.playlistmaker.playlist.presentation.PlaylistViewModel
import com.practicum.playlistmaker.search.presentation.TracksSearchViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.android.ext.koin.androidContext
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

    viewModel<PlaylistsViewModel> {
        PlaylistsViewModel(get())
    }

    viewModel<CreatePlaylistViewModel> {
        CreatePlaylistViewModel(get())
    }

    viewModel<PlaylistViewModel> {
        PlaylistViewModel(get(), get(), androidContext().resources)
    }
}