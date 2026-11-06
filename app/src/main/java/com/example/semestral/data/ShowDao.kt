package com.example.semestral.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowDao {
    @Query("SELECT * FROM shows ORDER BY addedAt DESC, id DESC")
    fun observeAll(): Flow<List<SavedShow>>

    @Query("SELECT * FROM shows WHERE id = :id")
    fun observeById(id: Int): Flow<SavedShow?>

    @Query("SELECT * FROM shows WHERE id = :id")
    suspend fun getById(id: Int): SavedShow?

    // Adding an existing show must never reset its status or create a duplicate.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(show: SavedShow)

    @Query("UPDATE shows SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: WatchStatus)

    @Query("DELETE FROM shows WHERE id = :id")
    suspend fun delete(id: Int)
}
