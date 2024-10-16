package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlaylist(album: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(album: PlaylistEntity)

    @Query("SELECT * FROM album_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTracksId(playlistEntity: PlaylistEntity)
}