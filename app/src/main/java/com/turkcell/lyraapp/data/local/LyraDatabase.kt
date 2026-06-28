package com.turkcell.lyraapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DownloadedSongEntity::class, RecentlyPlayedEntity::class, FavoritedSongEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class LyraDatabase : RoomDatabase() {
    abstract fun downloadedSongDao(): DownloadedSongDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun favoritedSongDao(): FavoritedSongDao
}
