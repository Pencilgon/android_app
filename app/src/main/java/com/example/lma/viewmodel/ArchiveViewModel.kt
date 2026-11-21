package com.example.lma.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lma.data.ArchiveDatabase
import com.example.lma.data.ArchiveRepository
import com.example.lma.data.RecoveryStatusUpdateResult
import com.example.lma.model.MediaCase
import com.example.lma.model.MediaCategory
import com.example.lma.model.RecoveryEvent
import com.example.lma.model.RecoveryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArchiveUiState(
    val cases: List<MediaCase> = emptyList(),
    val recoveryEvents: List<RecoveryEvent> = emptyList(),

    val searchQuery: String = "",
    val selectedCategory: MediaCategory? = null,
    val selectedStatus: RecoveryStatus? = null,

    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val saveCompleted: Boolean = false
) {

    val filteredCases: List<MediaCase>
        get() = cases.filter { mediaCase ->
            val matchesQuery =
                mediaCase.title.contains(searchQuery, ignoreCase = true) ||
                        mediaCase.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory =
                selectedCategory == null ||
                        mediaCase.category == selectedCategory

            val matchesStatus =
                selectedStatus == null ||
                        mediaCase.recoveryStatus == selectedStatus

            matchesQuery && matchesCategory && matchesStatus
        }

    val lostCount: Int
        get() = cases.count {
            it.recoveryStatus == RecoveryStatus.LOST
        }

    val partiallyFoundCount: Int
        get() = cases.count {
            it.recoveryStatus == RecoveryStatus.PARTIALLY_FOUND
        }

    val foundCount: Int
        get() = cases.count {
            it.recoveryStatus == RecoveryStatus.FOUND
        }

    val totalCases: Int
        get() = cases.size
}

class ArchiveViewModel(application: Application) :
    AndroidViewModel(application) {

    private val database =
        ArchiveDatabase.getDatabase(application)

    private val repository =
        ArchiveRepository(database)

    private val _uiState = MutableStateFlow(ArchiveUiState())

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.cases.collect { cases ->
                _uiState.update { currentState ->
                    currentState.copy(
                        cases = cases,
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.recoveryEvents.collect { events ->
                _uiState.update { currentState ->
                    currentState.copy(
                        recoveryEvents = events
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
    }

    fun updateCategory(category: MediaCategory?) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    fun updateStatusFilter(status: RecoveryStatus?) {
        _uiState.update {
            it.copy(selectedStatus = status)
        }
    }

    fun getCaseById(id: Int): MediaCase? {
        return _uiState.value.cases.find {
            it.id == id
        }
    }

    fun addCase(
        title: String,
        description: String,
        category: MediaCategory,
        lastKnownYear: Int?,
        sourceUrl: String?
    ) {
        val cleanTitle = title.trim()
        val cleanDescription = description.trim()

        if (cleanTitle.isBlank() || cleanDescription.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Title and description are required.",
                    successMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                repository.addCase(
                    MediaCase(
                        id = 0,
                        title = cleanTitle,
                        description = cleanDescription,
                        category = category,
                        lastKnownYear = lastKnownYear,
                        recoveryStatus = RecoveryStatus.LOST,
                        sourceUrl = sourceUrl
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() },
                        isBookmarked = false
                    )
                )

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Case added successfully.",
                        saveCompleted = true,
                        searchQuery = "",
                        selectedCategory = null,
                        selectedStatus = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to save case: ${e.message}",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun updateCase(
        id: Int,
        title: String,
        description: String,
        category: MediaCategory,
        lastKnownYear: Int?,
        sourceUrl: String?
    ) {
        val cleanTitle = title.trim()
        val cleanDescription = description.trim()

        if (cleanTitle.isBlank() || cleanDescription.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Title and description are required.",
                    successMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                val existingCase = repository.getCaseById(id)

                if (existingCase == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Case not found.",
                            successMessage = null
                        )
                    }
                    return@launch
                }

                repository.updateCase(
                    existingCase.copy(
                        title = cleanTitle,
                        description = cleanDescription,
                        category = category,
                        lastKnownYear = lastKnownYear,
                        sourceUrl = sourceUrl
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() }
                    )
                )

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Case updated successfully.",
                        saveCompleted = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to update case: ${e.message}",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun deleteCase(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteCase(id)

                _uiState.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Case deleted successfully."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to delete case: ${e.message}",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun updateRecoveryStatus(
        id: Int,
        status: RecoveryStatus,
        note: String = ""
    ) {
        viewModelScope.launch {
            try {
                val result = repository.updateRecoveryStatus(
                    id = id,
                    newStatus = status,
                    note = note
                )

                when (result) {
                    is RecoveryStatusUpdateResult.Success -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = null,
                                successMessage =
                                    "Recovery status updated: " +
                                            "${result.previousStatus.displayName} → " +
                                            result.newStatus.displayName
                            )
                        }
                    }

                    RecoveryStatusUpdateResult.NoChange -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = null,
                                successMessage =
                                    "Status is already set to ${status.displayName}."
                            )
                        }
                    }

                    RecoveryStatusUpdateResult.CaseNotFound -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = "Case not found.",
                                successMessage = null
                            )
                        }
                    }

                    is RecoveryStatusUpdateResult.InvalidTransition -> {
                        _uiState.update {
                            it.copy(
                                errorMessage =
                                    "Cannot change status from " +
                                            "${result.currentStatus.displayName} to " +
                                            "${result.requestedStatus.displayName}.",
                                successMessage = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to update status: ${e.message}",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun toggleBookmark(id: Int) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(id)
            } catch (_: Exception) {
                // Bookmark changes do not display notifications.
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(successMessage = null)
        }
    }

    fun clearSaveCompleted() {
        _uiState.update {
            it.copy(saveCompleted = false)
        }
    }
}