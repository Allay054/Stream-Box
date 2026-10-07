package com.allay.streambox.data.remote

import com.allay.streambox.data.remote.api.ChannelApi
import com.allay.streambox.data.remote.model.RemoteChannel
import com.allay.streambox.data.remote.model.RemoteStream

class RemoteChannelDataSource(
    private val api: ChannelApi
) {

    suspend fun getChannels(): List<RemoteChannel> {
        return api.getChannels()
    }

    suspend fun getStreams(): List<RemoteStream> {
        return api.getStreams()
    }
}