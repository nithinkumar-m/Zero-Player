package com.zeroplayer.domain.repository

import com.zeroplayer.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun observeDeviceVideos(): Flow<List<Video>>

    /** Re-scan MediaStore and emit the latest list. */
    suspend fun scanDeviceVideos()

    /** Backwards-compatible alias used by some call sites. */
    suspend fun refreshDeviceVideos() = scanDeviceVideos()
}
