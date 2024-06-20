package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.toDomain
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> emit(Resource.Error("Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"))
            200 -> {
                with(response as TrackSearchResponse) {
                    val data = results.toDomain()
                    emit(Resource.Success(data))
                }
            }
            else -> emit(Resource.Error("Ошибка сервера"))
        }
    }
}