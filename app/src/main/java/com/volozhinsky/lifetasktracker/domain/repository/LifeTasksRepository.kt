package com.volozhinsky.lifetasktracker.domain.repository

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList

interface LifeTasksRepository {

    suspend fun getTaskLists(): List<TaskList>

    suspend fun getSelectedTaskList(): TaskList

    suspend fun getTasks(): List<Task>
}