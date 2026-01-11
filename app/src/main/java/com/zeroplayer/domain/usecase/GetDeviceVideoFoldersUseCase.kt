package com.zeroplayer.domain.usecase

import com.zeroplayer.domain.model.VideoFolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDeviceVideoFoldersUseCase @Inject constructor(
    private val getDeviceVideos: GetDeviceVideosUseCase,
) {
    operator fun invoke(): Flow<List<VideoFolder>> {
        return getDeviceVideos().map { videos ->
            videos
                .groupBy { it.bucketId }
                .map { (bucketId, bucketVideos) ->
                    val name = bucketVideos.firstOrNull()?.bucketName?.ifBlank { "Unknown" } ?: "Unknown"
                    VideoFolder(
                        bucketId = bucketId,
                        name = name,
                        videoCount = bucketVideos.size,
                        thumbnailUriString = bucketVideos.firstOrNull()?.uriString,
                    )
                }
                .sortedWith(
                    compareByDescending<VideoFolder> { it.videoCount }
                        .thenBy { it.name.lowercase() },
                )
        }
    }
}

