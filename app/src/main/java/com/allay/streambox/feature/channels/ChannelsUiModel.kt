package com.allay.streambox.feature.channels

data class ChannelCategory(
    val id: String,
    val title: String
)

data class ChannelItem(
    val id: String,
    val name: String,
    val categoryId: String,
    val description: String = "",
    val streamUrl: String? = null,
    val streamType: String? = null,
    val isLive: Boolean = true
)
//
//val demoChannelCategories = listOf(
//    ChannelCategory("all", "All"),
//    ChannelCategory("news", "News"),
//    ChannelCategory("sports", "Sports"),
//    ChannelCategory("entertainment", "Entertainment"),
//    ChannelCategory("music", "Music"),
//    ChannelCategory("documentary", "Documentary")
//)
//
//val demoChannelItems = listOf(
//    ChannelItem(
//        id = "1",
//        name = "World News",
//        categoryId = "news",
//        description = "International news"
//    ),
//    ChannelItem(
//        id = "2",
//        name = "Live Sports",
//        categoryId = "sports",
//        description = "Live sports"
//    ),
//    ChannelItem(
//        id = "3",
//        name = "Entertainment",
//        categoryId = "entertainment",
//        description = "Entertainment"
//    ),
//    ChannelItem(
//        id = "4",
//        name = "Music Live",
//        categoryId = "music",
//        description = "Live music"
//    ),
//    ChannelItem(
//        id = "5",
//        name = "Documentary",
//        categoryId = "documentary",
//        description = "Documentaries"
//    ),
//    ChannelItem(
//        id = "6",
//        name = "International News",
//        categoryId = "news",
//        description = "International news"
//    )
//)