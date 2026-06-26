package com.turkcell.lyraapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorited_songs")
data class FavoritedSongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val startColor: Long,
    val endColor: Long,
    val durationMs: Long,
    val isSynced: Boolean = true
)
