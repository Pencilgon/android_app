package com.example.lma.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lma.viewmodel.ArchiveViewModel
import com.example.lma.ui.components.StatusChip
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailsScreen(
    caseId: Int,
    viewModel: ArchiveViewModel,
    onBack: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val mediaCase = uiState.cases.find {
        it.id == caseId
    }

    val caseHistory = uiState.recoveryEvents.filter {
        it.mediaCaseId == caseId
    }

    var statusMenuExpanded by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var statusNote by remember {
        mutableStateOf("")
    }

    if (mediaCase == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Case not found",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "This case may have been deleted."
            )

            Button(onClick = onBack) {
                Text("Go Back")
            }
        }

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CASE #${mediaCase.id.toString().padStart(3, '0')}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = mediaCase.title,
            style = MaterialTheme.typography.headlineMedium
        )

        StatusChip(
            status = mediaCase.recoveryStatus
        )

        DetailSection(
            title = "Category",
            value = mediaCase.category.displayName
        )

        DetailSection(
            title = "Description",
            value = mediaCase.description
        )

        DetailSection(
            title = "Last Known Year",
            value = mediaCase.lastKnownYear?.toString() ?: "Unknown"
        )

        DetailSection(
            title = "Source URL",
            value = mediaCase.sourceUrl ?: "No source provided"
        )

        OutlinedButton(
            onClick = {
                viewModel.toggleBookmark(caseId)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (mediaCase.isBookmarked) {
                    "★ Remove from Watchlist"
                } else {
                    "☆ Add to Watchlist"
                }
            )
        }

        Text(
            text = "Update Recovery Status",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Choose the next valid recovery stage.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ExposedDropdownMenuBox(
            expanded = statusMenuExpanded,
            onExpandedChange = {
                statusMenuExpanded = !statusMenuExpanded
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = mediaCase.recoveryStatus.displayName,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Current Recovery Status")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = statusMenuExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = statusMenuExpanded,
                onDismissRequest = {
                    statusMenuExpanded = false
                }
            ) {
                mediaCase.recoveryStatus
                    .allowedNextStatuses()
                    .forEach { status ->
                        DropdownMenuItem(
                            text = {
                                Text(status.displayName)
                            },
                            onClick = {
                                viewModel.updateRecoveryStatus(
                                    id = caseId,
                                    status = status,
                                    note = statusNote.trim()
                                )

                                statusNote = ""
                                statusMenuExpanded = false
                            }
                        )
                    }
            }
        }

        OutlinedTextField(
            value = statusNote,
            onValueChange = {
                statusNote = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Status Change Note (Optional)")
            },
            placeholder = {
                Text("What was discovered?")
            },
            minLines = 2,
            maxLines = 4
        )

        Text(
            text = "Recovery History",
            style = MaterialTheme.typography.titleLarge
        )

        if (caseHistory.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "No recovery status changes recorded yet. " +
                            "Updates to this case will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            caseHistory.forEach { event ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = event.previousStatus.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text("→")

                            Text(
                                text = event.newStatus.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (event.note.isNotBlank()) {
                            Text(
                                text = event.note,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = DateFormat.getDateTimeInstance().format(
                                Date(event.createdAt)
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                onEditClick(caseId)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Case")
        }

        OutlinedButton(
            onClick = {
                showDeleteDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Delete Case",
                color = MaterialTheme.colorScheme.error
            )
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Case?")
            },
            text = {
                Text(
                    "Are you sure you want to delete " +
                            "\"${mediaCase.title}\"? " +
                            "This will also delete its recovery history."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCase(caseId)
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailSection(
    title: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}