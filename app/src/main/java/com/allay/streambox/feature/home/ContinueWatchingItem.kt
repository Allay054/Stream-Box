package com.allay.streambox.feature.home

data class ContinueWatchingItem(
    val channel: HomeChannel,
    val positionMs: Long,
    val durationMs: Long
)
