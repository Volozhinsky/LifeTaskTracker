package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.TaskEntity
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.domain.models.Task
import java.time.LocalDateTime
import javax.inject.Inject

class TaskMapper @Inject constructor() {
    fun mapEntityToDomain(response: TaskEntity): Task = with(response) {
        Task(
            id = id,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = status,
            due = due,
            position = position
        )
    }

    fun mapResponseToEntity(response: TaskResponse, account: String,listId: String): TaskEntity = with(response) {
        TaskEntity(
            account = account,
            listId = listId,
            id = id ?: "",
            title = title ?: "",
            selfLink = selfLink ?: "",
            parent =parent ?: "",
            notes = notes ?: "",
            status = status ?: "",
            due = LocalDateTime.parse(due) ?: LocalDateTime.MIN,
            position = position?.toInt() ?: 0
        )
    }
}