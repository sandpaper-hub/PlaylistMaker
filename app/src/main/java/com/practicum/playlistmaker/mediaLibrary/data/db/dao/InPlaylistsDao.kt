package com.practicum.playlistmaker.mediaLibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediaLibrary.data.db.entity.TrackInPlaylistsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrack(track: TrackInPlaylistsEntity)

    @Query("SELECT * FROM in_playlists_table")
    fun getAllTracks(): Flow<List<TrackInPlaylistsEntity>>

   @Query("DELETE FROM in_playlists_table WHERE id = :trackId")
    suspend fun removeTrackEntity(trackId: Int)
}