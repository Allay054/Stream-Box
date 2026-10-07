package com.allay.streambox.data.local

import com.allay.streambox.domain.model.Channel
import com.allay.streambox.domain.model.ChannelCategory

class DemoChannelDataSource {

    private val categories = listOf(
        ChannelCategory(
            id = "all",
            name = "All"
        ),
        ChannelCategory(
            id = "news",
            name = "News"
        ),
        ChannelCategory(
            id = "sports",
            name = "Sports"
        ),
        ChannelCategory(
            id = "entertainment",
            name = "Entertainment"
        ),
        ChannelCategory(
            id = "music",
            name = "Music"
        ),
        ChannelCategory(
            id = "documentary",
            name = "Documentary"
        )
    )

    private val channels = listOf(
        Channel(
            id = "1",
            name = "World News",
            categoryId = "news",
            description = "International news",
            isLive = true
        ),
        Channel(
            id = "2",
            name = "Live Sports",
            categoryId = "sports",
            description = "Live sports",
            isLive = true
        ),
        Channel(
            id = "3",
            name = "Entertainment",
            categoryId = "entertainment",
            description = "Entertainment",
            isLive = true
        ),
        Channel(
            id = "4",
            name = "Music Live",
            categoryId = "music",
            description = "Live music",
            isLive = true
        ),
        Channel(
            id = "5",
            name = "Documentary",
            categoryId = "documentary",
            description = "Documentaries",
            isLive = true
        ),
        Channel(
            id = "6",
            name = "International News",
            categoryId = "news",
            description = "International news",
            isLive = true
        )
    )

    fun getChannels(): List<Channel> {
        return channels
    }

    fun getChannelCategories(): List<ChannelCategory> {
        return categories
    }

    fun getChannelById(channelId: String): Channel? {
        return channels.firstOrNull {
            it.id == channelId
        }
    }
}