package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.repository.FavoriteRepository

class AddFavoriteUseCase(
    private val repository: FavoriteRepository
) {

    suspend operator fun invoke(
        channelId: String
    ) {
        repository.addFavorite(channelId)
    }
}