package com.allay.streambox.feature.home

data class HomeCategory(
    val id: String,
    val title: String
)

data class HomeChannel(
    val id: String,
    val name: String,
    val categoryId: String,
    val description: String = "",
    val streamUrl: String? = null,
    val streamType: String? = null,
    val isLive: Boolean = true
)