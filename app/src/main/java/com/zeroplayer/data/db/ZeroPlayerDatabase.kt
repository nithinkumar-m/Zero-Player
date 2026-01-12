package com.zeroplayer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlaybackResumeEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ZeroPlayerDatabase : RoomDatabase() {
    abstract fun playbackResumeDao(): PlaybackResumeDao
}

