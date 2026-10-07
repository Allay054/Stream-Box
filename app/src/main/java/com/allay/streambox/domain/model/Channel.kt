package com.allay.streambox.domain.model

data class Channel(
    val id: String,
    val name: String,
    val categoryId: String,
    val description: String,
    val logoUrl: String? = null,
    val streamSource: StreamSource? = null,
    val isLive: Boolean = true
)