package com.practicum.playlistmaker

import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchApi {
    @GET("/search?entity=song ")
    fun search(@Query("term") text: String)
}