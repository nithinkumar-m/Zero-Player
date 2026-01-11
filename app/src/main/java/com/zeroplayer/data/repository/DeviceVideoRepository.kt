package com.zeroplayer.data.repository

import com.zeroplayer.data.mediastore.MediaStoreVideoDataSource
import com.zeroplayer.domain.model.Video
import com.zeroplayer.domain.repository.VideoRepository
import com.zeroplayer.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceVideoRepository @Inject constructor(
    private val mediaStoreVideoDataSource: MediaStoreVideoDataSource,
    @ApplicationScope private val appScope: CoroutineScope,
) : VideoRepository {
    private val videosState = MutableStateFlow<List<Video>?>(null)

    init {
        appScope.launch(Dispatchers.IO) {
            videosState.value = mediaStoreVideoDataSource.queryDeviceVideos()
        }
    }

    override fun observeDeviceVideos(): Flow<List<Video>> = videosState.filterNotNull()

    override suspend fun refreshDeviceVideos() {
        val fresh = withContext(Dispatchers.IO) {
            mediaStoreVideoDataSource.queryDeviceVideos()
        }
        videosState.value = fresh
    }
}

