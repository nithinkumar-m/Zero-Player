package com.zeroplayer.domain.model

data class VideoFolder(
    val bucketId: Long,
    val name: String,
    val videoCount: Int,
    val thumbnailUriString: String?,
)

