package com.practicum.playlistmaker.mediaLibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.InPlaylistsDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediaLibrary.data.db.dao.TrackDao
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistsEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistsEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun inPlaylistsDao(): InPlaylistsDao
}