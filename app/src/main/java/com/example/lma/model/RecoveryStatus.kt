package com.example.lma.model

enum class RecoveryStatus(val displayName: String) {
    LOST("Lost"),
    PARTIALLY_FOUND("Partially Found"),
    FOUND("Found");

    fun allowedNextStatuses(): Set<RecoveryStatus> {
        return when (this) {
            LOST -> setOf(PARTIALLY_FOUND)

            PARTIALLY_FOUND -> setOf(
                LOST,
                FOUND
            )

            FOUND -> setOf(PARTIALLY_FOUND)
        }
    }

    fun canTransitionTo(newStatus: RecoveryStatus): Boolean {
        return newStatus in allowedNextStatuses()
    }
}