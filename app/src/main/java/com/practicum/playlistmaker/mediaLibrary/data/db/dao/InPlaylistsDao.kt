package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistsEntity

@Dao
interface InPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(track: TrackInPlaylistsEntity)

    @Query("SELECT * FROM in_playlists_table")
    suspend fun getAllTracks(): List<TrackInPlaylistsEntity>
}