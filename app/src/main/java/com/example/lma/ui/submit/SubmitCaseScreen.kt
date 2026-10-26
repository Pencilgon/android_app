package com.example.lma.ui.submit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lma.model.MediaCategory
import com.example.lma.viewmodel.ArchiveViewModel

@Composable
fun SubmitCaseScreen(
    viewModel: ArchiveViewModel,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable {
        mutableStateOf(MediaCategory.OTHER.name)
    }
    var yearText by rememberSaveable { mutableStateOf("") }
    var sourceUrl by rememberSaveable { mutableStateOf("") }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }

    val category = MediaCategory.valueOf(selectedCategory)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back")
            }

            Text(
                text = "CONTRIBUTE TO THE ARCHIVE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Submit a case",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Document a piece of media that may be lost, " +
                        "incomplete, or difficult to find.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    validationError = null
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title *") },
                placeholder = { Text("Name of the missing media") },
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    validationError = null
                    viewModel.clearError()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description *") },
                placeholder = {
                    Text("What is known about this case?")
                },
                minLines = 4
            )

            Column {
                Text(
                    text = "MEDIA CATEGORY",
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = category.displayName,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    TextButton(
                        onClick = {
                            categoryMenuExpanded = true
                        }
                    ) {
                        Text("Change")
                    }
                }

                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = {
                        categoryMenuExpanded = false
                    }
                ) {
                    MediaCategory.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                selectedCategory = option.name
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = yearText,
                onValueChange = {
                    yearText = it.filter(Char::isDigit).take(4)
                    validationError = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Last known year (optional)") },
                placeholder = { Text("e.g. 2004") },
                singleLine = true
            )

            OutlinedTextField(
                value = sourceUrl,
                onValueChange = {
                    sourceUrl = it
                    validationError = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Source URL (optional)") },
                placeholder = { Text("https://...") },
                singleLine = true
            )

            if (validationError != null) {
                Text(
                    text = validationError.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    val parsedYear = yearText.toIntOrNull()

                    when {
                        title.isBlank() || description.isBlank() -> {
                            validationError =
                                "Please enter both a title and description."
                        }

                        yearText.isNotBlank() &&
                                (parsedYear == null ||
                                        parsedYear !in 1..9999) -> {
                            validationError =
                                "Enter a valid year between 1 and 9999."
                        }

                        sourceUrl.isNotBlank() &&
                                !sourceUrl.trim().startsWith("https://") &&
                                !sourceUrl.trim().startsWith("http://") -> {
                            validationError =
                                "The source URL must start with http:// or https://."
                        }

                        else -> {
                            validationError = null

                            viewModel.addCase(
                                title = title,
                                description = description,
                                category = category,
                                lastKnownYear = parsedYear,
                                sourceUrl = sourceUrl
                            )

                            onSubmitted()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit to archive")
            }

            Text(
                text = "* Required fields",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}