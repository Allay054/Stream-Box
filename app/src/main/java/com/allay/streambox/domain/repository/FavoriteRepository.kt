package com.allay.streambox.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun addFavorite(
        channelId: String
    )

    suspend fun removeFavorite(
        channelId: String
    )

    suspend fun isFavorite(
        channelId: String
    ): Boolean

    fun observeFavoriteChannelIds(): Flow<List<String>>

    suspend fun getFavoriteChannelIds(): List<String>
}