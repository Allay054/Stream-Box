package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.model.PlaybackProgress
import com.allay.streambox.domain.repository.PlaybackProgressRepository

class GetPlaybackProgressUseCase(
    private val repository: PlaybackProgressRepository
) {
    suspend operator fun invoke(
        channelId: String
    ): PlaybackProgress? {
        return repository.getProgress(channelId)
    }
}
