package com.volozhinsky.lifetasktracker.ui

import com.volozhinsky.lifetasktracker.domain.models.Task

interface GoogleTasksRepository {

    suspend fun synchronizeTaskLists()

    suspend fun getTask(taskInternalId: String):Task

    suspend fun saveTask(task: Task)
}