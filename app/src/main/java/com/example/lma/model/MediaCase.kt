package com.example.lma.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_cases")
data class MediaCase(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val category: MediaCategory,
    val lastKnownYear: Int?,
    val recoveryStatus: RecoveryStatus,
    val sourceUrl: String?,
    val isBookmarked: Boolean = false
)