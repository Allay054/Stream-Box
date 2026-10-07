package com.allay.streambox.data.repository

import com.allay.streambox.data.local.DemoChannelDataSource
import com.allay.streambox.data.remote.RemoteChannelDataSource
import com.allay.streambox.domain.model.Channel
import com.allay.streambox.domain.model.ChannelCategory
import com.allay.streambox.domain.model.StreamSource
import com.allay.streambox.domain.model.StreamType
import com.allay.streambox.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChannelRepositoryImpl(
    private val demoDataSource: DemoChannelDataSource,
    private val remoteDataSource: RemoteChannelDataSource
) : ChannelRepository {

    override fun getChannels(): Flow<List<Channel>> = flow {

        try {

            val remoteChannels =
                remoteDataSource.getChannels()

            val remoteStreams =
                remoteDataSource.getStreams()

            val streamsByChannelId =
                remoteStreams
                    .filter { stream ->
                        !stream.channel.isNullOrBlank() &&
                                stream.url.isNotBlank()
                    }
                    .groupBy { stream ->
                        stream.channel!!
                    }

            val channels =
                remoteChannels
                    .filter { channel ->
                        !channel.isNsfw
                    }
                    .mapNotNull { remoteChannel ->

                        val availableStreams =
                            streamsByChannelId[
                                remoteChannel.id
                            ]

                        val stream =
                            availableStreams
                                ?.firstOrNull()

                        if (stream == null) {
                            return@mapNotNull null
                        }

                        Channel(
                            id = remoteChannel.id,
                            name = remoteChannel.name,
                            categoryId =
                                remoteChannel.categories
                                    .firstOrNull()
                                    ?: "other",
                            description =
                                remoteChannel.network
                                    ?: remoteChannel.name,
                            logoUrl = null,
                            streamSource =
                                StreamSource(
                                    url = stream.url,
                                    type = detectStreamType(stream.url),
                                    referrer = stream.referrer,
                                    userAgent = stream.userAgent
                                ),
                            isLive = true
                        )
                    }

            emit(channels)

        } catch (exception: Exception) {

            emit(
                demoDataSource.getChannels()
            )
        }
    }

    override fun getChannelCategories():
            Flow<List<ChannelCategory>> = flow {

        try {

            val remoteChannels =
                remoteDataSource.getChannels()

            val remoteStreams =
                remoteDataSource.getStreams()

            val channelsWithStreams =
                remoteStreams
                    .mapNotNull { stream ->
                        stream.channel
                    }
                    .toSet()

            val categories =
                remoteChannels
                    .filter { channel ->
                        !channel.isNsfw &&
                                channel.id in channelsWithStreams
                    }
                    .flatMap { channel ->
                        channel.categories
                    }
                    .distinct()
                    .sorted()
                    .map { category ->

                        ChannelCategory(
                            id = category,
                            name = category
                                .replaceFirstChar {
                                    it.uppercase()
                                }
                        )
                    }

            emit(categories)

        } catch (exception: Exception) {

            emit(
                demoDataSource
                    .getChannelCategories()
            )
        }
    }

    override suspend fun getChannelById(
        channelId: String
    ): Channel? {

        return try {

            val remoteChannels =
                remoteDataSource.getChannels()

            val remoteStreams =
                remoteDataSource.getStreams()

            val remoteChannel =
                remoteChannels.firstOrNull {
                    it.id == channelId
                }

            if (remoteChannel == null) {
                return demoDataSource
                    .getChannelById(channelId)
            }

            val stream =
                remoteStreams
                    .filter { remoteStream ->
                        remoteStream.channel ==
                                remoteChannel.id &&
                                remoteStream.url.isNotBlank()
                    }
                    .firstOrNull()

            if (stream == null) {
                return null
            }

            Channel(
                id = remoteChannel.id,
                name = remoteChannel.name,
                categoryId =
                    remoteChannel.categories
                        .firstOrNull()
                        ?: "other",
                description =
                    remoteChannel.network
                        ?: remoteChannel.name,
                logoUrl = null,
                streamSource =
                    StreamSource(
                        url = stream.url,
                        type = detectStreamType(stream.url),
                        referrer = stream.referrer,
                        userAgent = stream.userAgent
                    ),
                isLive = true
            )

        } catch (exception: Exception) {

            demoDataSource
                .getChannelById(channelId)
        }
    }

    private fun detectStreamType(
        url: String
    ): StreamType {

        return when {

            url.contains(
                ".m3u8",
                ignoreCase = true
            ) -> {
                StreamType.HLS
            }

            url.contains(
                ".mpd",
                ignoreCase = true
            ) -> {
                StreamType.DASH
            }

            else -> {
                StreamType.PROGRESSIVE
            }
        }
    }
}