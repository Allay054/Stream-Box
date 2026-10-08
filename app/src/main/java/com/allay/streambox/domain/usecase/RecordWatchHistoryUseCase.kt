package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.WatchHistoryRepository

class RecordWatchHistoryUseCase(
    private val repository: WatchHistoryRepository
) {

    suspend operator fun invoke(
        channelId: String
    ) {
        repository.recordChannelWatched(
            channelId
        )
    }
}