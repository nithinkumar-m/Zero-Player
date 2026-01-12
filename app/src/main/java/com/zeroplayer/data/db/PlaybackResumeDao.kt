package com.zeroplayer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaybackResumeDao {
    @Query("SELECT * FROM playback_resume WHERE uriString = :uriString LIMIT 1")
    suspend fun get(uriString: String): PlaybackResumeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PlaybackResumeEntity)
}

