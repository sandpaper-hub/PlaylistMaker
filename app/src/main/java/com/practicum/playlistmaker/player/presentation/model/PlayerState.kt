package com.practicum.playlistmaker.player.presentation.model

interface PlayerState {
    data class Prepared(val position: String) : PlayerState
    object Playing : PlayerState
    object Pause : PlayerState
    object Complete : PlayerState
    data class Favorite(val inFavorite: Boolean): PlayerState

    data class Created(val inFavorite: Boolean) : PlayerState

    data class ChangePosition(val position: String) : PlayerState

}