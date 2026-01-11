package com.zeroplayer.data.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.zeroplayer.domain.model.Video
import javax.inject.Inject

class MediaStoreVideoDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
) {
    fun queryDeviceVideos(): List<Video> {
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val videos = ArrayList<Video>(256)
        contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder,
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(nameCol).orEmpty()
                val durationMs = cursor.getLong(durationCol)
                val sizeBytes = cursor.getLong(sizeCol)
                val dateAdded = cursor.getLong(dateAddedCol)

                val contentUri = ContentUris.withAppendedId(collection, id)

                videos += Video(
                    id = id,
                    title = title,
                    uriString = contentUri.toString(),
                    durationMs = durationMs,
                    sizeBytes = sizeBytes,
                    dateAddedEpochSeconds = dateAdded,
                )
            }
        }

        return videos
    }
}

