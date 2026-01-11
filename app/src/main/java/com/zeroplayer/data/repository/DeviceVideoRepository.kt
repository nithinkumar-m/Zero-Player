package com.zeroplayer.data.repository

import com.zeroplayer.data.mediastore.MediaStoreVideoDataSource
import com.zeroplayer.domain.model.Video
import com.zeroplayer.domain.repository.VideoRepository
import com.zeroplayer.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceVideoRepository @Inject constructor(
    private val mediaStoreVideoDataSource: MediaStoreVideoDataSource,
    @ApplicationScope private val appScope: CoroutineScope,
) : VideoRepository {
    // Shared, cached scan to avoid re-querying MediaStore per screen/viewmodel.
    private val sharedVideos: Flow<List<Video>> = flow {
        emit(mediaStoreVideoDataSource.queryDeviceVideos())
    }
        .flowOn(Dispatchers.IO)
        .shareIn(appScope, started = SharingStarted.WhileSubscribed(5_000), replay = 1)

    override fun observeDeviceVideos(): Flow<List<Video>> = sharedVideos
}

