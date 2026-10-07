package com.allay.streambox.data.remote.api

import com.allay.streambox.data.remote.model.RemoteChannel
import com.allay.streambox.data.remote.model.RemoteStream
import retrofit2.http.GET

interface ChannelApi {

    @GET("channels.json")
    suspend fun getChannels(): List<RemoteChannel>

    @GET("streams.json")
    suspend fun getStreams(): List<RemoteStream>
}