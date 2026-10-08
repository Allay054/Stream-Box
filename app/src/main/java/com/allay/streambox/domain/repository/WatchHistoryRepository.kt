package com.allay.streambox.domain.repository

import kotlinx.coroutines.flow.Flow

interface WatchHistoryRepository {

    suspend fun recordChannelWatched(
        channelId: String
    )

    fun observeHistoryChannelIds(
        limit: Int
    ): Flow<List<String>>

    suspend fun getHistoryChannelIds(): List<String>

    suspend fun clearHistory()

    suspend fun removeHistory(
        channelId: String
    )
}