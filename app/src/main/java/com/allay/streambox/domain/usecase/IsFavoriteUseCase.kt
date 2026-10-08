package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.FavoriteRepository

class IsFavoriteUseCase(
    private val repository: FavoriteRepository
) {

    suspend operator fun invoke(
        channelId: String
    ): Boolean {
        return repository.isFavorite(channelId)
    }
}