package com.allay.streambox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.allay.streambox.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertHistory(
        history: WatchHistoryEntity
    )

    @Query(
        """
        SELECT channelId
        FROM watch_history
        ORDER BY watchedAt DESC
        LIMIT :limit
        """
    )
    fun observeHistoryChannelIds(
        limit: Int
    ): Flow<List<String>>

    @Query(
        """
        SELECT channelId
        FROM watch_history
        ORDER BY watchedAt DESC
        """
    )
    suspend fun getHistoryChannelIds(): List<String>

    @Query(
        """
        DELETE FROM watch_history
        """
    )
    suspend fun clearHistory()

    @Query(
        """
        DELETE FROM watch_history
        WHERE channelId = :channelId
        """
    )
    suspend fun deleteHistory(
        channelId: String
    )
}