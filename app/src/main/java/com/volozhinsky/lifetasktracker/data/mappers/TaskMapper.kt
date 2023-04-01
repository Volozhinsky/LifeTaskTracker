package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.TaskEntity
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.domain.models.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class TaskMapper @Inject constructor(private val dateTimeFormatter : DateTimeFormatter) {
    fun mapEntityToDomain(response: TaskEntity): Task = with(response) {
        Task(
            id = id,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = status == COMPLETE_STRING,
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
            due =due?.let {
                LocalDateTime.parse(it,dateTimeFormatter)
            }   ?: LocalDateTime.MIN,
            position = position?.toInt() ?: 0
        )
    }

    companion object{

        const val COMPLETE_STRING = "completed"
    }
}