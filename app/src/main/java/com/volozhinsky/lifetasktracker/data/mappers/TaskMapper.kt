package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.TaskEntity
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.domain.models.Task
import java.time.LocalDateTime
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
            }   ?: LocalDateTime.MIN,
            position = position?.toInt() ?: 0
        )
    }

    fun mapDomainToResponseCreate(response: Task): TaskResponse = with(response) {
        TaskResponse(

            id = null,
            title = null,
            selfLink = null,
            parent =null,
            notes = null,
            status = null, //if(status) COMPLETE_STRING else NEEDS_ACTION,
            due =null, //due.format(dateTimeFormatter),
            position = null //position.toString()
        )
    }

    companion object{

        const val COMPLETE_STRING = "completed"
        const val NEEDS_ACTION =  "needsAction"
    }
}