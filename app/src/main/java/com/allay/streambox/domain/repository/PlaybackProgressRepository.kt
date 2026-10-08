package com.allay.streambox.domain.repository

import com.allay.streambox.domain.model.PlaybackProgress
import kotlinx.coroutines.flow.Flow

interface PlaybackProgressRepository {
    suspend fun saveProgress(
        channelId: String,
        positionMs: Long,
        durationMs: Long
    )

    suspend fun getProgress(channelId: String): PlaybackProgress?

    fun observeContinueWatching(limit: Int): Flow<List<PlaybackProgress>>

    suspend fun deleteProgress(channelId: String)
}
