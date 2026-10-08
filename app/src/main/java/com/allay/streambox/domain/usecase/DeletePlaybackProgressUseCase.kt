package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.PlaybackProgressRepository

class DeletePlaybackProgressUseCase(
    private val repository: PlaybackProgressRepository
) {
    suspend operator fun invoke(channelId: String) {
        repository.deleteProgress(channelId)
    }
}
