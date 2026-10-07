package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.model.Channel
import com.allay.streambox.domain.repository.ChannelRepository

class GetChannelByIdUseCase(
    private val repository: ChannelRepository
) {

    suspend operator fun invoke(
        channelId: String
    ): Channel? {
        return repository.getChannelById(channelId)
    }
}