package com.example.lma.data

import com.example.lma.model.MediaCase
import com.example.lma.model.MediaCategory
import com.example.lma.model.RecoveryStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArchiveRepository {

    private val mutex = Mutex()

    private val _cases = MutableStateFlow(SampleCases.cases)
    val cases = _cases.asStateFlow()

    suspend fun getCaseById(id: Int): MediaCase? {
        return cases.value.find { it.id == id }
    }

    suspend fun addCase(mediaCase: MediaCase) {
        mutex.withLock {
            _cases.update { currentCases ->
                currentCases + mediaCase
            }
        }
    }

    suspend fun updateRecoveryStatus(
        id: Int,
        status: RecoveryStatus
    ) {
        mutex.withLock {
            _cases.update { currentCases ->
                currentCases.map { mediaCase ->
                    if (mediaCase.id == id) {
                        mediaCase.copy(recoveryStatus = status)
                    } else {
                        mediaCase
                    }
                }
            }
        }
    }

    suspend fun toggleBookmark(id: Int) {
        mutex.withLock {
            _cases.update { currentCases ->
                currentCases.map { mediaCase ->
                    if (mediaCase.id == id) {
                        mediaCase.copy(
                            isBookmarked = !mediaCase.isBookmarked
                        )
                    } else {
                        mediaCase
                    }
                }
            }
        }
    }

    suspend fun searchCases(
        query: String,
        category: MediaCategory? = null,
        status: RecoveryStatus? = null
    ): List<MediaCase> {
        delay(100)

        return cases.value.filter { mediaCase ->
            val matchesQuery =
                mediaCase.title.contains(query, ignoreCase = true) ||
                        mediaCase.description.contains(query, ignoreCase = true)

            val matchesCategory =
                category == null || mediaCase.category == category

            val matchesStatus =
                status == null || mediaCase.recoveryStatus == status

            matchesQuery && matchesCategory && matchesStatus
        }
    }
}