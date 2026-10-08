package com.allay.streambox.data.repository

import com.allay.streambox.data.local.dao.WatchHistoryDao
import com.allay.streambox.data.local.entity.WatchHistoryEntity
import com.allay.streambox.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow

class WatchHistoryRepositoryImpl(
    private val watchHistoryDao: WatchHistoryDao
) : WatchHistoryRepository {

    override suspend fun recordChannelWatched(
        channelId: String
    ) {
        watchHistoryDao.insertHistory(
            WatchHistoryEntity(
                channelId = channelId,
                watchedAt = System.currentTimeMillis()
            )
        )
    }

    override fun observeHistoryChannelIds(
        limit: Int
    ): Flow<List<String>> {
        return watchHistoryDao.observeHistoryChannelIds(
            limit = limit
        )
    }

    override suspend fun getHistoryChannelIds(): List<String> {
        return watchHistoryDao.getHistoryChannelIds()
    }

    override suspend fun clearHistory() {
        watchHistoryDao.clearHistory()
    }

    override suspend fun removeHistory(
        channelId: String
    ) {
        watchHistoryDao.deleteHistory(
            channelId
        )
    }
}