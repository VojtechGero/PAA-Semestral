package com.example.semestral.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shows")
data class SavedShow(
    @PrimaryKey val id: Int,
    val name: String,
    val summary: String,
    val imageUrl: String?,
    val genres: String,
    val premiered: String?,
    val rating: Double?,
    val tvmazeUrl: String,
    val status: WatchStatus = WatchStatus.PLAN_TO_WATCH,
    val addedAt: Long = System.currentTimeMillis()
)
