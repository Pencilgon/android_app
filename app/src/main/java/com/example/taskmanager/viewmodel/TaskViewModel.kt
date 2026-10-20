package com.example.taskmanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.taskmanager.model.Task
import com.example.taskmanager.model.TaskInfo
import com.example.taskmanager.model.TaskStatus

class TaskViewModel : ViewModel() {

    var tasks by mutableStateOf(
        listOf(
            TaskInfo(
                Task.WorkTask(
                    id = 1,
                    title = "Create Kotlin project",
                    priority = 5,
                    hours = 3.0,
                    company = "University"
                ),
                TaskStatus.IN_PROGRESS
            ),
            TaskInfo(
                Task.WorkTask(
                    id = 2,
                    title = "Write backend API",
                    priority = 4,
                    hours = 5.0,
                    company = "Company"
                )
            ),
            TaskInfo(
                Task.PersonalTask(
                    id = 3,
                    title = "Go to the gym",
                    priority = 2,
                    hours = 1.5
                )
            )
        )
    )
        private set

    private var nextId = 4

    var searchQuery by mutableStateOf("")
        private set

    var selectedStatus by mutableStateOf<TaskStatus?>(null)
        private set

    fun addTask(
        title: String,
        priority: Int,
        hours: Double,
        isWorkTask: Boolean,
        company: String = ""
    ) {
        val task = if (isWorkTask) {
            Task.WorkTask(
                id = nextId++,
                title = title,
                priority = priority,
                hours = hours,
                company = company
            )
        } else {
            Task.PersonalTask(
                id = nextId++,
                title = title,
                priority = priority,
                hours = hours
            )
        }

        tasks = tasks + TaskInfo(task)
    }

    fun completeTask(id: Int) {
        tasks = tasks.map { taskInfo ->
            if (taskInfo.task.id == id) {
                taskInfo.copy(status = TaskStatus.COMPLETED)
            } else {
                taskInfo
            }
        }
    }

    fun updateStatus(id: Int, status: TaskStatus) {
        tasks = tasks.map { taskInfo ->
            if (taskInfo.task.id == id) {
                taskInfo.copy(status = status)
            } else {
                taskInfo
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun updateStatusFilter(status: TaskStatus?) {
        selectedStatus = status
    }

    fun getFilteredTasks(): List<TaskInfo> {
        return tasks.filter { taskInfo ->
            val matchesSearch = taskInfo.task.title.contains(
                searchQuery,
                ignoreCase = true
            )

            val matchesStatus = selectedStatus == null ||
                    taskInfo.status == selectedStatus

            matchesSearch && matchesStatus
        }
    }

    fun getTotalHours(): Double {
        return tasks.sumOf { it.task.hours }
    }
}