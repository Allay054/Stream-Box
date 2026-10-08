package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveWatchHistoryUseCase(
    private val repository: WatchHistoryRepository
) {

    operator fun invoke(
        limit: Int
    ): Flow<List<String>> {
        return repository.observeHistoryChannelIds(
            limit = limit
        )
    }
}