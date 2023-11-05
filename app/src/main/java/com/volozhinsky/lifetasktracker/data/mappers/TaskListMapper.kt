package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.TaskListEntity
import com.volozhinsky.lifetasktracker.data.models.TaskListResponse
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.User
import javax.inject.Inject

class TaskListMapper @Inject constructor() {
    fun mapEntityToDomain(response: TaskListEntity, user: User): TaskList = with(response) {
        TaskList(
            title = title,
            id = id,
            user = user
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

    fun mapResponseToDomain(response: TaskListResponse, user: User): TaskList = with(response) {
        TaskList(
            id = id ?: "",
            title = title ?: "",
            user = user
        )
    }
}