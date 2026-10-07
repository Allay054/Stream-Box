package com.allay.streambox.domain.repository

import com.allay.streambox.domain.model.Channel
import com.allay.streambox.domain.model.ChannelCategory
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {

    fun getChannels(): Flow<List<Channel>>

    fun getChannelCategories(): Flow<List<ChannelCategory>>

    suspend fun getChannelById(
        channelId: String
    ): Channel?
}