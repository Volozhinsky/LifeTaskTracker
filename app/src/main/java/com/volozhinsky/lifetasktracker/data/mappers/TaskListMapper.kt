package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.TaskListEntity
import com.volozhinsky.lifetasktracker.data.models.TaskListResponse
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import javax.inject.Inject

class TaskListMapper @Inject constructor() {
    fun mapEntityToDomain(response: TaskListEntity): TaskList = with(response) {
        TaskList(
            title = title,
            id = id
        )
    }

    fun mapResponseToEntity(response: TaskListResponse, account: String): TaskListEntity = with(response) {
        TaskListEntity(
            id = id ?: "",
            title = title ?: "",
            selfLink = selfLink ?: "",
            account = account
        )
    }
}