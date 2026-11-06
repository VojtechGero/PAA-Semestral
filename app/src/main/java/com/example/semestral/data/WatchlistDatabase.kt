package com.example.semestral.data

import androidx.room.Database
import androidx.room.RoomDatabase

// Room supports enum <-> String conversion automatically.
@Database(entities = [SavedShow::class], version = 1, exportSchema = true)
abstract class WatchlistDatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
}
