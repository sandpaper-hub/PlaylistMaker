package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.util.createArrayListFromJson
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return tracksRepository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun addTrackToHistory(track: Track) {
        val json = sharedPreferencesRepository.getHistoryJson()
        val historyArray = json?.createArrayListFromJson() ?: ArrayList()
        historyArray.removeIf { it == track }
        historyArray.add(0, track)
        if (historyArray.size > 10) {
            historyArray.removeAt(10)
        }
        sharedPreferencesRepository.saveArrayListToHistory(historyArray)
    }

    override fun getHistory(): ArrayList<Track> {
        val json = sharedPreferencesRepository.getHistoryJson()
        return json?.createArrayListFromJson() ?: ArrayList()
    }

    override fun clearHistory() {
        val json = sharedPreferencesRepository.getHistoryJson()
        if (json != null) {
            val array = json.createArrayListFromJson()
            array.clear()
            sharedPreferencesRepository.saveArrayListToHistory(array)
        }
    }
}