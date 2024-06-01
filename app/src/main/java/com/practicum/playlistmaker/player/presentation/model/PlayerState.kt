package com.practicum.playlistmaker.player.presentation.model

interface PlayerState {
    data class Prepared(val position: String) : PlayerState
    object Playing : PlayerState
    object Pause : PlayerState
    object Complete : PlayerState

    object Created : PlayerState

    data class ChangePosition(val position: String) : PlayerState

}