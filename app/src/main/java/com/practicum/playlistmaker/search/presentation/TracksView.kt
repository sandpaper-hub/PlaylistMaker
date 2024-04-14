package com.practicum.playlistmaker.search.presentation

import com.practicum.playlistmaker.search.domain.models.TracksState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface TracksView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: TracksState)
}