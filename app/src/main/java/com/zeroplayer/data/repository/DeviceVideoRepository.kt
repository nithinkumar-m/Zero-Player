package com.zeroplayer.data.repository

import com.zeroplayer.data.mediastore.MediaStoreVideoDataSource
import com.zeroplayer.domain.model.Video
import com.zeroplayer.domain.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeviceVideoRepository @Inject constructor(
    private val mediaStoreVideoDataSource: MediaStoreVideoDataSource,
) : VideoRepository {
    override fun observeDeviceVideos(): Flow<List<Video>> = flow {
        emit(mediaStoreVideoDataSource.queryDeviceVideos())
    }.flowOn(Dispatchers.IO)
}

