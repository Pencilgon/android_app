package com.example.lma.ui.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lma.model.MediaCategory
import com.example.lma.model.RecoveryStatus
import com.example.lma.ui.components.MediaCaseCard
import com.example.lma.viewmodel.ArchiveViewModel

@Composable
fun ArchiveScreen(
    viewModel: ArchiveViewModel,
    onCaseClick: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onWatchlistClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "LOST MEDIA / ARCHIVE",
                    modifier = Modifier.padding(top = 24.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Unfound.\nNot forgotten.",
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = "A catalogue of missing media, lost recordings, " +
                            "and forgotten digital artefacts.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSubmitClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("＋ Submit case")
                    }

                    TextButton(
                        onClick = onWatchlistClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("★ Watchlist")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search the archive") },
                placeholder = { Text("Enter a title or keyword") },
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TextButton(
                        onClick = {
                            categoryMenuExpanded = true
                        }
                    ) {
                        Text(
                            uiState.selectedCategory?.displayName
                                ?: "All categories"
                        )
                    }

                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = {
                            categoryMenuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All categories") },
                            onClick = {
                                viewModel.updateCategory(null)
                                categoryMenuExpanded = false
                            }
                        )

                        MediaCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    viewModel.updateCategory(category)
                                    categoryMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    TextButton(
                        onClick = {
                            statusMenuExpanded = true
                        }
                    ) {
                        Text(
                            uiState.selectedStatus?.displayName
                                ?: "All statuses"
                        )
                    }

                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = {
                            statusMenuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All statuses") },
                            onClick = {
                                viewModel.updateStatusFilter(null)
                                statusMenuExpanded = false
                            }
                        )

                        RecoveryStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.displayName) },
                                onClick = {
                                    viewModel.updateStatusFilter(status)
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "${uiState.filteredCases.size} CASES FOUND",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (uiState.isLoading) {
                Text(
                    text = "Loading archive...",
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else if (uiState.filteredCases.isEmpty()) {
                Text(
                    text = "No cases match your search. Try changing the filters.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredCases,
                        key = { mediaCase -> mediaCase.id }
                    ) { mediaCase ->
                        MediaCaseCard(
                            mediaCase = mediaCase,
                            onClick = {
                                onCaseClick(mediaCase.id)
                            },
                            onBookmarkClick = {
                                viewModel.toggleBookmark(mediaCase.id)
                            }
                        )
                    }
                }
            }
        }
    }
}