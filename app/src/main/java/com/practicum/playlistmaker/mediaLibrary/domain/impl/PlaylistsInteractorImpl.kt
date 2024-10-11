package com.practicum.playlistmaker.mediaLibrary.domain.impl

import com.practicum.playlistmaker.mediaLibrary.domain.db.PlaylistsInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.db.PlaylistsRepository
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun updatePlaylistIds(playlist: Playlist, track: Track) {
        playlistsRepository.updatePlaylistTracksId(playlist, track)
    }
}