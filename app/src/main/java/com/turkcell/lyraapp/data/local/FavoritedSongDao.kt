package com.turkcell.lyraapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritedSongDao {
    @Query("SELECT * FROM favorited_songs")
    fun observeAll(): Flow<List<FavoritedSongEntity>>

    @Query("SELECT * FROM favorited_songs")
    suspend fun getAll(): List<FavoritedSongEntity>

    @Query("SELECT * FROM favorited_songs WHERE isSynced = 0")
    suspend fun getUnsynced(): List<FavoritedSongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: FavoritedSongEntity)

    @Query("DELETE FROM favorited_songs WHERE id = :id")
    suspend fun deleteById(id: String)
    
    @Query("UPDATE favorited_songs SET isSynced = :synced WHERE id = :id")
    suspend fun updateSyncStatus(id: String, synced: Boolean)
}
