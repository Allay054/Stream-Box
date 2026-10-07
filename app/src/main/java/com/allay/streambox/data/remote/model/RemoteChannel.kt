package com.allay.streambox.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteChannel(
    val id: String,
    val name: String,
    @SerialName("alt_names")
    val altNames: List<String> = emptyList(),
    val network: String? = null,
    val owners: List<String> = emptyList(),
    val country: String,
    val categories: List<String> = emptyList(),
    @SerialName("is_nsfw")
    val isNsfw: Boolean = false,
    val website: String? = null
)