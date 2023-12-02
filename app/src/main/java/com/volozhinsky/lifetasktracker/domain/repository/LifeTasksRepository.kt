package com.volozhinsky.lifetasktracker.domain.repository

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskFromWeb
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import com.volozhinsky.lifetasktracker.domain.models.User
import kotlinx.coroutines.flow.Flow

interface LifeTasksRepository {

    suspend fun getTaskLists(user: User): Flow<List<TaskList>>

    suspend fun getTasksFromTaskList(showComplete: Boolean,taskList: TaskList): Flow<List<Task>>

    suspend fun getTasksUnsinc(user: User): Flow<List<Task>>

    suspend fun insertAllIntoTask(tasks: List<Task>,taskList: TaskList)

    suspend fun startTimeLog(task: Task)

    suspend fun stopTimeLog()

    suspend fun getTimeLog(): Flow<List<TimeLog>>

    suspend fun getCurrentUser(): User

    fun getShowCompleteFlag(): Boolean

    fun getCurrentTaskList(user: User):TaskList
}