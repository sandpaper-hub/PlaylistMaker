package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.util.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> Resource.Error("Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету")
            200 -> Resource.Success((response as TrackSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            })
            else -> Resource.Error("Ошибка сервера")
        }
    }
}