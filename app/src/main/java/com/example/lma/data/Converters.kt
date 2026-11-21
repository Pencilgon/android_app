package com.example.lma.data

import androidx.room.TypeConverter
import com.example.lma.model.MediaCategory
import com.example.lma.model.RecoveryStatus

class Converters {

    @TypeConverter
    fun fromMediaCategory(category: MediaCategory): String {
        return category.name
    }

    @TypeConverter
    fun toMediaCategory(value: String): MediaCategory {
        return MediaCategory.valueOf(value)
    }

    @TypeConverter
    fun fromRecoveryStatus(status: RecoveryStatus): String {
        return status.name
    }

    @TypeConverter
    fun toRecoveryStatus(value: String): RecoveryStatus {
        return RecoveryStatus.valueOf(value)
    }
}