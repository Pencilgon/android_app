package com.example.lma.data

import com.example.lma.model.RecoveryStatus

sealed interface RecoveryStatusUpdateResult {

    data class Success(
        val previousStatus: RecoveryStatus,
        val newStatus: RecoveryStatus
    ) : RecoveryStatusUpdateResult

    data object NoChange : RecoveryStatusUpdateResult

    data object CaseNotFound : RecoveryStatusUpdateResult

    data class InvalidTransition(
        val currentStatus: RecoveryStatus,
        val requestedStatus: RecoveryStatus
    ) : RecoveryStatusUpdateResult
}