package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.model.ChannelCategory
import com.allay.streambox.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow

class GetChannelCategoriesUseCase(
    private val repository: ChannelRepository
) {

    operator fun invoke(): Flow<List<ChannelCategory>> {
        return repository.getChannelCategories()
    }
}