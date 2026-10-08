package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.PlaybackProgressRepository

class SavePlaybackProgressUseCase(
    private val repository: PlaybackProgressRepository
) {
    suspend operator fun invoke(
        channelId: String,
        positionMs: Long,
        durationMs: Long
    ) {
        repository.saveProgress(
            channelId = channelId,
            positionMs = positionMs,
            durationMs = durationMs
        )
    }
}
