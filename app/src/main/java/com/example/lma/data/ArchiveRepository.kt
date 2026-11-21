package com.example.lma.data

import androidx.room.withTransaction
import com.example.lma.model.MediaCase
import com.example.lma.model.RecoveryEvent
import com.example.lma.model.RecoveryStatus
import kotlinx.coroutines.flow.Flow

class ArchiveRepository(
    private val database: ArchiveDatabase
) {

    private val dao = database.archiveDao()

    val cases: Flow<List<MediaCase>> =
        dao.observeAllCases()

    val recoveryEvents: Flow<List<RecoveryEvent>> =
        dao.observeAllRecoveryEvents()

    suspend fun getCaseById(id: Int): MediaCase? {
        return dao.getCaseById(id)
    }

    suspend fun addCase(mediaCase: MediaCase) {
        dao.insertCase(mediaCase)
    }

    suspend fun updateCase(mediaCase: MediaCase) {
        dao.updateCase(mediaCase)
    }

    suspend fun deleteCase(id: Int) {
        dao.deleteCaseById(id)
    }

    suspend fun toggleBookmark(id: Int) {
        dao.toggleBookmark(id)
    }

    suspend fun updateRecoveryStatus(
        id: Int,
        newStatus: RecoveryStatus,
        note: String = ""
    ): RecoveryStatusUpdateResult {

        return database.withTransaction {

            val currentCase = dao.getCaseById(id)
                ?: return@withTransaction RecoveryStatusUpdateResult.CaseNotFound

            val previousStatus = currentCase.recoveryStatus

            if (previousStatus == newStatus) {
                return@withTransaction RecoveryStatusUpdateResult.NoChange
            }

            if (!previousStatus.canTransitionTo(newStatus)) {
                return@withTransaction RecoveryStatusUpdateResult.InvalidTransition(
                    currentStatus = previousStatus,
                    requestedStatus = newStatus
                )
            }

            dao.updateRecoveryStatus(id, newStatus)

            dao.insertRecoveryEvent(
                RecoveryEvent(
                    mediaCaseId = id,
                    previousStatus = previousStatus,
                    newStatus = newStatus,
                    note = note.trim()
                )
            )

            RecoveryStatusUpdateResult.Success(
                previousStatus = previousStatus,
                newStatus = newStatus
            )
        }
    }

    fun observeRecoveryHistory(
        caseId: Int
    ): Flow<List<RecoveryEvent>> {
        return dao.observeRecoveryHistory(caseId)
    }
}