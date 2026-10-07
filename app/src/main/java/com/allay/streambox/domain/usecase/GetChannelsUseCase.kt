package com.allay.streambox.domain.usecase

import com.allay.streambox.domain.model.Channel
import com.allay.streambox.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow

class GetChannelsUseCase(
    private val repository: ChannelRepository
) {

    operator fun invoke(): Flow<List<Channel>> {
        return repository.getChannels()
    }
}