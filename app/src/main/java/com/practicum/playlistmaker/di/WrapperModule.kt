package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.impl.MediaPlayerWrapperImpl
import com.practicum.playlistmaker.player.domain.wrapper.MediaPlayerWrapper
import org.koin.dsl.module

val wrapperModule = module {
    factory <MediaPlayerWrapper> {
        MediaPlayerWrapperImpl(get())
    }
}