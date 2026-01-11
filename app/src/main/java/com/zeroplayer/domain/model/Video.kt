package com.zeroplayer.domain.model

data class Video(
    val id: Long,
    val title: String,
    val uriString: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAddedEpochSeconds: Long,
)

