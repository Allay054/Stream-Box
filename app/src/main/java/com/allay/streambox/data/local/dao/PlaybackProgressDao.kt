package com.allay.streambox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.allay.streambox.data.local.entity.PlaybackProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: PlaybackProgressEntity)

    @Query("SELECT * FROM playback_progress WHERE channelId = :channelId LIMIT 1")
    suspend fun getProgress(channelId: String): PlaybackProgressEntity?

    @Query("""
        SELECT *
        FROM playback_progress
        WHERE positionMs > 0
          AND durationMs > 0
        ORDER BY updatedAt DESC
        LIMIT :limit
    """)
    fun observeContinueWatching(limit: Int): Flow<List<PlaybackProgressEntity>>

    @Query("DELETE FROM playback_progress WHERE channelId = :channelId")
    suspend fun deleteProgress(channelId: String)

    @Query("DELETE FROM playback_progress")
    suspend fun clearProgress()
}
