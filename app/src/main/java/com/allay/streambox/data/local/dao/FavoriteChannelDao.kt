package com.allay.streambox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.allay.streambox.data.local.entity.FavoriteChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(
        favorite: FavoriteChannelEntity
    )

    @Query(
        "DELETE FROM favorite_channels WHERE channelId = :channelId"
    )
    suspend fun deleteFavorite(
        channelId: String
    )

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM favorite_channels
            WHERE channelId = :channelId
        )
        """
    )
    suspend fun isFavorite(
        channelId: String
    ): Boolean

    @Query(
        """
        SELECT channelId
        FROM favorite_channels
        ORDER BY createdAt DESC
        """
    )
    fun observeFavoriteChannelIds(): Flow<List<String>>

    @Query(
        """
        SELECT channelId
        FROM favorite_channels
        ORDER BY createdAt DESC
        """
    )
    suspend fun getFavoriteChannelIds(): List<String>
}