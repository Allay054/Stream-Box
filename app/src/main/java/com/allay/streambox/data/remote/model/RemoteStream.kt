package com.allay.streambox.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteStream(
    val channel: String? = null,
    val feed: String? = null,
    val title: String,
    val url: String,
    val referrer: String? = null,
    @SerialName("user_agent")
    val userAgent: String? = null,
    val quality: String? = null,
    val labels: List<String> = emptyList()
)