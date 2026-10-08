package com.allay.streambox.data.repository

import com.allay.streambox.data.local.dao.FavoriteChannelDao
import com.allay.streambox.data.local.entity.FavoriteChannelEntity
import com.allay.streambox.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class FavoriteRepositoryImpl(
    private val favoriteChannelDao: FavoriteChannelDao
) : FavoriteRepository {

    override suspend fun addFavorite(
        channelId: String
    ) {
        favoriteChannelDao.insertFavorite(
            FavoriteChannelEntity(
                channelId = channelId
            )
        )
    }

    override suspend fun removeFavorite(
        channelId: String
    ) {
        favoriteChannelDao.deleteFavorite(channelId)
    }

    override suspend fun isFavorite(
        channelId: String
    ): Boolean {
        return favoriteChannelDao.isFavorite(channelId)
    }

    override fun observeFavoriteChannelIds(): Flow<List<String>> {
        return favoriteChannelDao.observeFavoriteChannelIds()
    }

    override suspend fun getFavoriteChannelIds(): List<String> {
        return favoriteChannelDao.getFavoriteChannelIds()
    }
}