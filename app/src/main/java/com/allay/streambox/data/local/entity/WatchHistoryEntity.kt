package com.allay.streambox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "watch_history"
)
data class WatchHistoryEntity(

    @PrimaryKey
    val channelId: String,

    val watchedAt: Long = System.currentTimeMillis()
)