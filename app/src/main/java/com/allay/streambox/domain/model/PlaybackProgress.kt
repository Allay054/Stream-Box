package com.allay.streambox.domain.model

data class PlaybackProgress(
    val channelId: String,
    val positionMs: Long,
    val durationMs: Long,
    val updatedAt: Long
)
