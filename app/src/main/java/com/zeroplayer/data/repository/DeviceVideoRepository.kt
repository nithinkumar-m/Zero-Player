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
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceVideoRepository @Inject constructor(
    private val mediaStoreVideoDataSource: MediaStoreVideoDataSource,
    @ApplicationScope private val appScope: CoroutineScope,
) : VideoRepository {
    private val videosState: MutableStateFlow<List<Video>> = MutableStateFlow(emptyList())
    private val videos = videosState.asStateFlow()

    init {
        // First scan happens off the main thread.
        appScope.launch(Dispatchers.IO) {
            videosState.value = mediaStoreVideoDataSource.queryDeviceVideos()
        }
    }

    override fun observeDeviceVideos(): Flow<List<Video>> = videos

    override suspend fun scanDeviceVideos() {
        val fresh = withContext(Dispatchers.IO) { mediaStoreVideoDataSource.queryDeviceVideos() }
        videosState.value = fresh
    }
}

