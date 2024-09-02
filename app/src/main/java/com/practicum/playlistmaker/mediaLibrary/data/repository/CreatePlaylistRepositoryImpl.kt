package com.practicum.playlistmaker.mediaLibrary.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.practicum.playlistmaker.mediaLibrary.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediaLibrary.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.domain.db.CreatePlaylistRepository
import com.practicum.playlistmaker.mediaLibrary.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistRepositoryImpl(
    private val context: Context,
    private val playlistDbConverter: PlaylistDbConverter,
    private val appDatabase: AppDatabase): CreatePlaylistRepository{
    override suspend fun addNewPlaylist(playlist: Playlist){
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun saveCover(uriString: String, fileName: String): String {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "albumCoverFiles")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val uri = Uri.parse(uriString)
        val file = File(filePath, "$fileName")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.path
    }
}