package com.example.lma.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.lma.model.MediaCase
import com.example.lma.model.RecoveryEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchiveDao {

    // READ: observe all media cases
    @Query("SELECT * FROM media_cases ORDER BY id ASC")
    fun observeAllCases(): Flow<List<MediaCase>>

    // READ: get one case
    @Query("SELECT * FROM media_cases WHERE id = :id")
    suspend fun getCaseById(id: Int): MediaCase?

    // CREATE: insert a case
    @Insert
    suspend fun insertCase(mediaCase: MediaCase): Long

    // CREATE: insert multiple initial cases
    @Insert
    suspend fun insertCases(cases: List<MediaCase>)

    // UPDATE: update a complete case
    @Update
    suspend fun updateCase(mediaCase: MediaCase)

    // UPDATE: update only the recovery status
    @Query("""
        UPDATE media_cases
        SET recoveryStatus = :status
        WHERE id = :id
    """)
    suspend fun updateRecoveryStatus(
        id: Int,
        status: com.example.lma.model.RecoveryStatus
    )

    // UPDATE: bookmark
    @Query("""
        UPDATE media_cases
        SET isBookmarked = NOT isBookmarked
        WHERE id = :id
    """)
    suspend fun toggleBookmark(id: Int)

    // DELETE: remove a case
    @Delete
    suspend fun deleteCase(mediaCase: MediaCase)

    // DELETE: remove by ID
    @Query("DELETE FROM media_cases WHERE id = :id")
    suspend fun deleteCaseById(id: Int)

    // READ: observe recovery history
    @Query("""
        SELECT * FROM recovery_events
        WHERE mediaCaseId = :caseId
        ORDER BY createdAt DESC
    """)
    fun observeRecoveryHistory(caseId: Int): Flow<List<RecoveryEvent>>

    // READ: observe all history
    @Query("SELECT * FROM recovery_events ORDER BY createdAt DESC")
    fun observeAllRecoveryEvents(): Flow<List<RecoveryEvent>>

    // CREATE: save a history entry
    @Insert
    suspend fun insertRecoveryEvent(event: RecoveryEvent)
}