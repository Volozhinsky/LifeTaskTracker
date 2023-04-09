package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.TaskEntity
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.domain.models.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class TaskMapper @Inject constructor(private val dateTimeFormatter : DateTimeFormatter) {
    fun mapEntityToDomain(response: TaskEntity): Task = with(response) {
        Task(
            id = id,
            internalId = internalId,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = status == COMPLETE_STRING,
            due = due,
            position = position
        )
    }

    fun mapDomainToEntity(response: Task, account: String,listId: String): TaskEntity = with(response) {
        TaskEntity(
            account = account,
            listId = listId,
            id = id,
            internalId = internalId,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = if(status) COMPLETE_STRING else NEEDS_ACTION,
            due = due,
            position = position,
            sinc = false
        )
    }


    fun mapResponseToEntity(response: TaskResponse, account: String,listId: String,internalId: UUID): TaskEntity = with(response) {
        TaskEntity(
            account = account,
            listId = listId,
            id = id ?: "",
            internalId = internalId,
            title = title ?: "",
            selfLink = selfLink ?: "",
            parent =parent ?: "",
            notes = notes ?: "",
            status = status ?: "",
            due =due?.let {
                LocalDateTime.parse(it,dateTimeFormatter)
            }   ?: LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneOffset.UTC),
            position = position?.toInt() ?: 0,
            sinc = true
        )
    }

    fun mapEntityToResponse(response: TaskEntity): TaskResponse = with(response) {
        TaskResponse(
            id = id,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = status,
            due = due.format(dateTimeFormatter),
            position = position.toString(),
        )
    }

    fun mapEntityToResponseCreate(response: TaskEntity): TaskResponse = with(response) {
        TaskResponse(
            id = null,
            title = title,
            selfLink = null,
            parent =null,
            notes = notes,
            status = status, //if(status) COMPLETE_STRING else NEEDS_ACTION,
            due =due.format(dateTimeFormatter), //due.format(dateTimeFormatter),
            position = null //position.toString()
        )
    }

    companion object{

        const val COMPLETE_STRING = "completed"
        const val NEEDS_ACTION =  "needsAction"
    }
}