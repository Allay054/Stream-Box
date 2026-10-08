package com.allay.streambox.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.allay.streambox.data.local.dao.FavoriteChannelDao
import com.allay.streambox.data.local.dao.WatchHistoryDao
import com.allay.streambox.data.local.entity.FavoriteChannelEntity
import com.allay.streambox.data.local.entity.WatchHistoryEntity

@Database(
    entities = [
        FavoriteChannelEntity::class,
        WatchHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StreamBoxDatabase : RoomDatabase() {

    abstract fun favoriteChannelDao(): FavoriteChannelDao

    abstract fun watchHistoryDao(): WatchHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: StreamBoxDatabase? = null

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS watch_history (
                            channelId TEXT NOT NULL,
                            watchedAt INTEGER NOT NULL,
                            PRIMARY KEY(channelId)
                        )
                        """.trimIndent()
                    )
                }
            }

        fun getInstance(
            context: Context
        ): StreamBoxDatabase {

            return INSTANCE
                ?: synchronized(this) {

                    INSTANCE
                        ?: Room.databaseBuilder(
                            context.applicationContext,
                            StreamBoxDatabase::class.java,
                            "streambox.db"
                        )
                            .addMigrations(
                                MIGRATION_1_2
                            )
                            .build()
                            .also {
                                INSTANCE = it
                            }
                }
        }
    }
}