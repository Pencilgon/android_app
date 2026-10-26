package com.example.lma.model

data class MediaCase(
    val id: Int,
    val title: String,
    val description: String,
    val category: MediaCategory,
    val lastKnownYear: Int?,
    val recoveryStatus: RecoveryStatus,
    val sourceUrl: String?,
    val isBookmarked: Boolean = false
)