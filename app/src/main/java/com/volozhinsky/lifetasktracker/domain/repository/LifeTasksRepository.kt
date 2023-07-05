package com.volozhinsky.lifetasktracker.domain.repository

import androidx.lifecycle.LiveData
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog

interface LifeTasksRepository {

    fun getTaskLists(): LiveData<List<TaskList>>

    suspend fun getTasks(): LiveData<List<Task>>

    suspend fun startTimeLog(task: Task)

    suspend fun stopTimeLog()

    suspend fun getTimeLog(): List<TimeLog>

}