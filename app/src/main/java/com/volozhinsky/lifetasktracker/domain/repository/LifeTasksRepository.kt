package com.volozhinsky.lifetasktracker.domain.repository

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog

interface LifeTasksRepository {

    suspend fun getTaskLists(): List<TaskList>

    suspend fun getSelectedTaskList(): TaskList

    suspend fun getTasks(): List<Task>

    suspend fun startTimeLog(task: Task)

    suspend fun stopTimeLog()

    suspend fun getTimeLog(): List<TimeLog>

}