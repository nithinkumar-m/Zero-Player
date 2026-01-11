package com.zeroplayer.domain.usecase

import com.zeroplayer.domain.repository.VideoRepository
import javax.inject.Inject

class GetDeviceVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
) {
    operator fun invoke() = videoRepository.observeDeviceVideos()
}

