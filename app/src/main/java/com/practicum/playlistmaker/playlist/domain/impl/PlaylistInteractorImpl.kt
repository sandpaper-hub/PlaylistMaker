package com.practicum.playlistmaker.playlist.domain.impl

import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import com.practicum.playlistmaker.playlist.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.playlist.domain.db.PlaylistRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun getAllTracks(trackIds: List<String>): Flow<List<Track>> {
        return playlistRepository.getAllTracks(trackIds)
    }

    override suspend fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String): Flow<Pair<Playlist, List<Track>>> {
        return playlistRepository.deleteTrackFromPlaylist(trackId)
    }
}