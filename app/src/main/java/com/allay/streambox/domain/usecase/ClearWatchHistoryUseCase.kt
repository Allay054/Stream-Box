package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.WatchHistoryRepository

class ClearWatchHistoryUseCase(
    private val repository: WatchHistoryRepository
) {

    suspend operator fun invoke() {
        repository.clearHistory()
    }
}