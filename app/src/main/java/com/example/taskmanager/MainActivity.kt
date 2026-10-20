
package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanager.model.Task
import com.example.taskmanager.model.TaskInfo
import com.example.taskmanager.model.TaskStatus
import com.example.taskmanager.ui.AddTaskForm
import com.example.taskmanager.ui.theme.TaskManagerTheme
import com.example.taskmanager.viewmodel.TaskViewModel

private val tabLabels = listOf(
    "All",
    "To do",
    "In progress",
    "Completed",
    "Add task"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TaskManagerTheme {
                val taskViewModel: TaskViewModel = viewModel()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskScreen(taskViewModel)
                }
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    val tasks = viewModel.tasks
    val isAddTaskTab = selectedTab == 4
    val filteredTasks = viewModel.getFilteredTasks()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "My Tasks",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
            )

            Text(
                text = "Total tasks: ${tasks.size}  •  Estimated hours: ${
                    "%.1f".format(viewModel.getTotalHours())
                }",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTab) {
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index

                            when (index) {
                                0 -> viewModel.updateStatusFilter(null)
                                1 -> viewModel.updateStatusFilter(TaskStatus.TODO)
                                2 -> viewModel.updateStatusFilter(TaskStatus.IN_PROGRESS)
                                3 -> viewModel.updateStatusFilter(TaskStatus.COMPLETED)
                            }
                        },
                        text = { Text(label, maxLines = 1) }
                    )
                }
            }

            if (isAddTaskTab) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    AddTaskForm(
                        onAddTask = { title, priority, hours, isWorkTask, company ->
                            viewModel.addTask(
                                title = title,
                                priority = priority,
                                hours = hours,
                                isWorkTask = isWorkTask,
                                company = company
                            )

                            viewModel.updateSearchQuery("")
                            viewModel.updateStatusFilter(null)
                            selectedTab = 0
                        }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        label = { Text("Search tasks") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        singleLine = true
                    )

                    Text(
                        text = "Tasks (${filteredTasks.size})",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredTasks.isEmpty()) {
                        Text("No tasks match this view.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredTasks,
                                key = { it.task.id }
                            ) { taskInfo ->
                                TaskCard(
                                    taskInfo = taskInfo,
                                    onStatusChange = { newStatus ->
                                        viewModel.updateStatus(
                                            taskInfo.task.id,
                                            newStatus
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    taskInfo: TaskInfo,
    onStatusChange: (TaskStatus) -> Unit
) {
    var menuExpanded by rememberSaveable(taskInfo.task.id) {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = taskInfo.task.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Priority: ${taskInfo.task.priority}/5")
            Text("Estimated time: ${taskInfo.task.hours} hours")
            Text("Status: ${taskInfo.status.toDisplayName()}")

            when (val task = taskInfo.task) {
                is Task.WorkTask -> Text("Work: ${task.company}")
                is Task.PersonalTask -> Text("Personal task")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { menuExpanded = true }) {
                    Text("Change status")
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    TaskStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.toDisplayName()) },
                            onClick = {
                                onStatusChange(status)
                                menuExpanded = false
                            },
                            enabled = status != taskInfo.status
                        )
                    }
                }
            }
        }
    }
}

private fun TaskStatus.toDisplayName(): String {
    return when (this) {
        TaskStatus.TODO -> "To do"
        TaskStatus.IN_PROGRESS -> "In progress"
        TaskStatus.COMPLETED -> "Completed"
    }
}
