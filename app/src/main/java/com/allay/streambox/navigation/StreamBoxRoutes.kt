package com.allay.streambox.navigation

object StreamBoxRoutes {

    const val HOME = "home"

    const val FAVORITES = "favorites"

    const val HISTORY = "history"

    const val CHANNELS = "channels/{categoryId}"

    const val CHANNEL_DETAILS = "channel_details/{channelId}"

    const val PLAYER = "player/{channelId}"

    fun channels(categoryId: String): String =
        "channels/$categoryId"

    fun channelDetails(channelId: String): String =
        "channel_details/$channelId"

    fun player(channelId: String): String =
        "player/$channelId"
}