package com.volozhinsky.lifetasktracker.domain.repository

import androidx.lifecycle.LiveData
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import kotlinx.coroutines.flow.Flow

interface LifeTasksRepository {

    suspend fun getTaskLists(): Flow<List<TaskList>>

    suspend fun getTasksFromTaskList(showComplete: Boolean): Flow<List<Task>>

    suspend fun startTimeLog(task: Task)

    suspend fun stopTimeLog()

    suspend fun getTimeLog(): Flow<List<TimeLog>>
}