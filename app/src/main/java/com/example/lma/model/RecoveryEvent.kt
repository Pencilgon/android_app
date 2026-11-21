package com.example.lma.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recovery_events",
    foreignKeys = [
        ForeignKey(
            entity = MediaCase::class,
            parentColumns = ["id"],
            childColumns = ["mediaCaseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mediaCaseId")]
)
data class RecoveryEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val mediaCaseId: Int,
    val previousStatus: RecoveryStatus,
    val newStatus: RecoveryStatus,
    val note: String,
    val createdAt: Long = System.currentTimeMillis()
)