package com.allay.streambox.domain.model

data class StreamSource(
    val url: String,
    val type: StreamType,
    val referrer: String? = null,
    val userAgent: String? = null
)

enum class StreamType {
    HLS,
    DASH,
    PROGRESSIVE
}