package com.zeroplayer.domain.repository

import com.zeroplayer.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun observeDeviceVideos(): Flow<List<Video>>
}

