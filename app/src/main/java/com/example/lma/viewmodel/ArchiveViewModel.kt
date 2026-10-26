package com.example.lma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lma.data.ArchiveRepository
import com.example.lma.model.MediaCase
import com.example.lma.model.MediaCategory
import com.example.lma.model.RecoveryStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArchiveUiState(
    val cases: List<MediaCase> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: MediaCategory? = null,
    val selectedStatus: RecoveryStatus? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
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
}

class ArchiveViewModel : ViewModel() {

    private val repository = ArchiveRepository()

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.cases.collect { cases ->
                _uiState.update {
                    it.copy(
                        cases = cases,
                        isLoading = false
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

    fun updateRecoveryStatus(id: Int, status: RecoveryStatus) {
        viewModelScope.launch {
            repository.updateRecoveryStatus(id, status)
        }
    }

    fun toggleBookmark(id: Int) {
        viewModelScope.launch {
            repository.toggleBookmark(id)
        }
    }

    fun getCaseById(id: Int): MediaCase? {
        return _uiState.value.cases.find { it.id == id }
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
                it.copy(errorMessage = "Title and description are required.")
            }
            return
        }

        viewModelScope.launch {
            val nextId = (_uiState.value.cases.maxOfOrNull { it.id } ?: 0) + 1

            repository.addCase(
                MediaCase(
                    id = nextId,
                    title = cleanTitle,
                    description = cleanDescription,
                    category = category,
                    lastKnownYear = lastKnownYear,
                    recoveryStatus = RecoveryStatus.LOST,
                    sourceUrl = sourceUrl?.trim()?.takeIf { it.isNotEmpty() }
                )
            )

            _uiState.update {
                it.copy(
                    searchQuery = "",
                    selectedCategory = null,
                    selectedStatus = null,
                    errorMessage = null
                )
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}