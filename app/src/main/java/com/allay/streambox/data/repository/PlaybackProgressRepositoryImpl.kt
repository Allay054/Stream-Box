package com.allay.streambox.data.repository

import com.allay.streambox.data.local.dao.PlaybackProgressDao
import com.allay.streambox.data.local.entity.PlaybackProgressEntity
import com.allay.streambox.domain.model.PlaybackProgress
import com.allay.streambox.domain.repository.PlaybackProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaybackProgressRepositoryImpl(
    private val dao: PlaybackProgressDao
) : PlaybackProgressRepository {

    override suspend fun saveProgress(
        channelId: String,
        positionMs: Long,
        durationMs: Long
    ) {
        dao.saveProgress(
            PlaybackProgressEntity(
                channelId = channelId,
                positionMs = positionMs,
                durationMs = durationMs,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getProgress(
        channelId: String
    ): PlaybackProgress? {
        return dao.getProgress(channelId)?.toDomain()
    }

    override fun observeContinueWatching(
        limit: Int
    ): Flow<List<PlaybackProgress>> {
        return dao
            .observeContinueWatching(limit)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun deleteProgress(
        channelId: String
    ) {
        dao.deleteProgress(channelId)
    }

    private fun PlaybackProgressEntity.toDomain(): PlaybackProgress {
        return PlaybackProgress(
            channelId = channelId,
            positionMs = positionMs,
            durationMs = durationMs,
            updatedAt = updatedAt
        )
    }
}
