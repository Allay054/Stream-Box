package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.FavoriteRepository

class RemoveFavoriteUseCase(
    private val repository: FavoriteRepository
) {

    suspend operator fun invoke(
        channelId: String
    ) {
        repository.removeFavorite(channelId)
    }
}