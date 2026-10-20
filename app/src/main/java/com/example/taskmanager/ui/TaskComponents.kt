
package com.example.taskmanager.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskForm(
    onAddTask: (
        title: String,
        priority: Int,
        hours: Double,
        isWorkTask: Boolean,
        company: String
    ) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf("3") }
    var hours by rememberSaveable { mutableStateOf("1.0") }
    var isWorkTask by rememberSaveable { mutableStateOf(false) }
    var company by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Add a task",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = priority,
                onValueChange = { priority = it },
                label = { Text("Priority (1–5)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = hours,
                onValueChange = { hours = it },
                label = { Text("Estimated hours") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Work task")
                Checkbox(
                    checked = isWorkTask,
                    onCheckedChange = { isWorkTask = it }
                )
            }

            if (isWorkTask) {
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company or organization") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    val parsedPriority = priority.toIntOrNull()
                    val parsedHours = hours.toDoubleOrNull()

                    when {
                        title.isBlank() ->
                            errorMessage = "Enter a task title."

                        parsedPriority == null || parsedPriority !in 1..5 ->
                            errorMessage = "Priority must be between 1 and 5."

                        parsedHours == null ||
                                !parsedHours.isFinite() ||
                                parsedHours <= 0.0 ->
                            errorMessage = "Hours must be a positive number."

                        isWorkTask && company.isBlank() ->
                            errorMessage = "Enter the company or organization."

                        else -> {
                            onAddTask(
                                title.trim(),
                                parsedPriority,
                                parsedHours,
                                isWorkTask,
                                company.trim()
                            )

                            title = ""
                            priority = "3"
                            hours = "1.0"
                            isWorkTask = false
                            company = ""
                            errorMessage = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add task")
            }
        }
    }
}
