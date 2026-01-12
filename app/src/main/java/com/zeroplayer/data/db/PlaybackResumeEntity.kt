package com.zeroplayer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_resume")
data class PlaybackResumeEntity(
    @PrimaryKey val uriString: String,
    val positionMs: Long,
    val playbackSpeed: Float,
    val updatedAtEpochMs: Long,
)

