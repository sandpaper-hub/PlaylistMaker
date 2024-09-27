package com.practicum.playlistmaker.mediaLibrary.data.converters

import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistCover,
            playlist.tracksId,
            playlist.tracksCount
        )
    }

    private fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.id,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.playlistCover,
            playlistEntity.tracksId,
            playlistEntity.tracksCount
        )
    }

    fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> map(playlist) }
    }
}