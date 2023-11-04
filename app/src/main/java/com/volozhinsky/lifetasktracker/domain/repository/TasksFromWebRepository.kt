package com.volozhinsky.lifetasktracker.domain.repository

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.User

interface TasksFromWebRepository {
    suspend fun getTaskLists(user: User): List<TaskList>

    suspend fun getTasks(taskList: TaskList): List<Task>

    suspend fun addTask(task: Task,taskList: TaskList):Task

}