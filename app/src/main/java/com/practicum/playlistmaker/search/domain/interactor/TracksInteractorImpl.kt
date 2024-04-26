package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.util.createArrayListFromJson
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.practicum.playlistmaker.search.domain.toDomain
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) :
    TracksInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data?.toDomain(), null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
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