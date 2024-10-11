package com.practicum.playlistmaker.mediaLibrary.domain.impl

import com.practicum.playlistmaker.mediaLibrary.domain.db.FavoriteTracksInteractor
import com.practicum.playlistmaker.mediaLibrary.domain.db.FavoriteTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(private val favoriteTracksRepository: FavoriteTracksRepository):
    FavoriteTracksInteractor {
    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }
}