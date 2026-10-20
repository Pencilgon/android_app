package com.example.taskmanager.model

sealed class Task(
    open val id: Int,
    open val title: String,
    open val priority: Int,
    open val hours: Double
) {
    data class WorkTask(
        override val id: Int,
        override val title: String,
        override val priority: Int,
        override val hours: Double,
        val company: String
    ) : Task(id, title, priority, hours)

    data class PersonalTask(
        override val id: Int,
        override val title: String,
        override val priority: Int,
        override val hours: Double
    ) : Task(id, title, priority, hours)
}