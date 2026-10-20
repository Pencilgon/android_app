package com.example.taskmanager.model

data class TaskInfo(
    val task: Task,
    val status: TaskStatus = TaskStatus.TODO
)