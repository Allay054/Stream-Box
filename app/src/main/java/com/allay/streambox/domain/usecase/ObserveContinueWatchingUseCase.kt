package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.model.PlaybackProgress
import com.allay.streambox.domain.repository.PlaybackProgressRepository
import kotlinx.coroutines.flow.Flow

class ObserveContinueWatchingUseCase(
    private val repository: PlaybackProgressRepository
) {
    operator fun invoke(limit: Int): Flow<List<PlaybackProgress>> {
        return repository.observeContinueWatching(limit)
    }
}
