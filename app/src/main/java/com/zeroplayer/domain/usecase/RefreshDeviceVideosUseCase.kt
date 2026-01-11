package com.zeroplayer.domain.usecase

import com.zeroplayer.domain.repository.VideoRepository
import javax.inject.Inject

class RefreshDeviceVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
) {
    suspend operator fun invoke() {
        videoRepository.refreshDeviceVideos()
    }
}

