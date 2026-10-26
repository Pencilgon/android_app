package com.example.lma.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lma.model.RecoveryStatus
import com.example.lma.ui.components.StatusChip
import com.example.lma.viewmodel.ArchiveViewModel

@Composable
fun CaseDetailsScreen(
    caseId: Int,
    viewModel: ArchiveViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val mediaCase = uiState.cases.find { it.id == caseId }

    var statusMenuExpanded by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back to archive")
            }

            if (mediaCase == null) {
                Text(
                    text = "Case not found.",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "This case may have been removed or is no longer available."
                )
            } else {
                Text(
                    text = "ARCHIVE RECORD / #CASE-${
                        mediaCase.id.toString().padStart(3, '0')
                    }",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = mediaCase.title,
                    style = MaterialTheme.typography.headlineLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(status = mediaCase.recoveryStatus)

                    Text(
                        text = mediaCase.category.displayName,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "CASE DESCRIPTION",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = mediaCase.description,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "LAST KNOWN YEAR",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = mediaCase.lastKnownYear?.toString()
                                ?: "Unknown",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "SOURCE",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = mediaCase.sourceUrl ?: "No source provided",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text(
                    text = "UPDATE RECOVERY STATUS",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(
                    onClick = {
                        statusMenuExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change status: ${mediaCase.recoveryStatus.displayName}")
                }

                DropdownMenu(
                    expanded = statusMenuExpanded,
                    onDismissRequest = {
                        statusMenuExpanded = false
                    }
                ) {
                    RecoveryStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.displayName) },
                            onClick = {
                                viewModel.updateRecoveryStatus(
                                    id = mediaCase.id,
                                    status = status
                                )
                                statusMenuExpanded = false
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.toggleBookmark(mediaCase.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (mediaCase.isBookmarked) {
                            "★ Remove from watchlist"
                        } else {
                            "☆ Add to watchlist"
                        }
                    )
                }
            }
        }
    }
}