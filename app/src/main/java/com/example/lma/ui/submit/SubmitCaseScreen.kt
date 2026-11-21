package com.example.lma.ui.submit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lma.model.MediaCategory
import com.example.lma.viewmodel.ArchiveViewModel

@Composable
fun SubmitCaseScreen(
    viewModel: ArchiveViewModel,
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    caseId: Int? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val existingCase = caseId?.let {
        viewModel.getCaseById(it)
    }

    var title by rememberSaveable(caseId) {
        mutableStateOf(existingCase?.title ?: "")
    }

    var description by rememberSaveable(caseId) {
        mutableStateOf(existingCase?.description ?: "")
    }

    var selectedCategory by rememberSaveable(caseId) {
        mutableStateOf(
            existingCase?.category ?: MediaCategory.OTHER
        )
    }

    var year by rememberSaveable(caseId) {
        mutableStateOf(existingCase?.lastKnownYear?.toString() ?: "")
    }

    var sourceUrl by rememberSaveable(caseId) {
        mutableStateOf(existingCase?.sourceUrl ?: "")
    }

    var categoryMenuExpanded by remember {
        mutableStateOf(false)
    }

    var titleError by rememberSaveable {
        mutableStateOf(false)
    }

    var descriptionError by rememberSaveable {
        mutableStateOf(false)
    }

    var yearError by rememberSaveable {
        mutableStateOf(false)
    }

    var urlError by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(existingCase?.id) {
        if (existingCase != null) {
            title = existingCase.title
            description = existingCase.description
            selectedCategory = existingCase.category
            year = existingCase.lastKnownYear?.toString() ?: ""
            sourceUrl = existingCase.sourceUrl ?: ""
        }
    }

    LaunchedEffect(uiState.saveCompleted) {
        if (uiState.saveCompleted) {
            viewModel.clearSaveCompleted()
            onSubmitted()
        }
    }

    val isEditing = caseId != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isEditing) {
                "Edit Media Case"
            } else {
                "Submit a Lost Media Case"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (isEditing) {
                "Update the information about this lost media."
            } else {
                "Help the community document lost media."
            },
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Title")
            },
            placeholder = {
                Text("Enter the media title")
            },
            isError = titleError,
            supportingText = {
                if (titleError) {
                    Text("Title is required.")
                }
            },
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                descriptionError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Description")
            },
            placeholder = {
                Text("Describe the media and what is known about it")
            },
            minLines = 4,
            maxLines = 8,
            isError = descriptionError,
            supportingText = {
                if (descriptionError) {
                    Text("Description is required.")
                }
            }
        )

        Column {
            Text(
                text = "Category",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            OutlinedButton(
                onClick = {
                    categoryMenuExpanded = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedCategory.displayName
                )
            }

            DropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = {
                    categoryMenuExpanded = false
                }
            ) {
                MediaCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(category.displayName)
                        },
                        onClick = {
                            selectedCategory = category
                            categoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = year,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 4) {
                    year = newValue
                    yearError = false
                }

                if (newValue.isEmpty()) {
                    year = ""
                    yearError = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Last Known Year (Optional)")
            },
            placeholder = {
                Text("e.g. 1998")
            },
            singleLine = true,
            isError = yearError,
            supportingText = {
                if (yearError) {
                    Text("Enter a valid year between 1 and 9999.")
                }
            }
        )

        OutlinedTextField(
            value = sourceUrl,
            onValueChange = {
                sourceUrl = it
                urlError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Source URL (Optional)")
            },
            placeholder = {
                Text("https://example.com")
            },
            singleLine = true,
            isError = urlError,
            supportingText = {
                if (urlError) {
                    Text("URL must start with http:// or https://")
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    titleError = title.isBlank()
                    descriptionError = description.isBlank()

                    yearError =
                        year.isNotBlank() &&
                                (
                                        year.toIntOrNull() == null ||
                                                year.toIntOrNull() !in 1..9999
                                        )

                    urlError =
                        sourceUrl.isNotBlank() &&
                                !sourceUrl.startsWith("http://", ignoreCase = true) &&
                                !sourceUrl.startsWith("https://", ignoreCase = true)

                    val isValid =
                        !titleError &&
                                !descriptionError &&
                                !yearError &&
                                !urlError

                    if (isValid) {
                        val parsedYear = year.toIntOrNull()

                        if (caseId == null) {
                            viewModel.addCase(
                                title = title,
                                description = description,
                                category = selectedCategory,
                                lastKnownYear = parsedYear,
                                sourceUrl = sourceUrl
                            )
                        } else {
                            viewModel.updateCase(
                                id = caseId,
                                title = title,
                                description = description,
                                category = selectedCategory,
                                lastKnownYear = parsedYear,
                                sourceUrl = sourceUrl
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (isEditing) {
                        "Save Changes"
                    } else {
                        "Submit Case"
                    }
                )
            }
        }
    }
}