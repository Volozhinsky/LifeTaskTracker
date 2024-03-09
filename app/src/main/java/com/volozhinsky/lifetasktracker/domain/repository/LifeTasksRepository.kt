package com.volozhinsky.lifetasktracker.domain.repository

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskFromWeb
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import com.volozhinsky.lifetasktracker.domain.models.User
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

interface LifeTasksRepository {

    suspend fun getTaskLists(user: User): Flow<List<TaskList>>

    suspend fun getTasksFromTaskList(showComplete: Boolean,taskList: TaskList): Flow<List<Task>>

    suspend fun getTasksUnsinc(user: User): Flow<List<Task>>

    suspend fun insertAllIntoTask(tasks: List<Task>,taskList: TaskList)

    suspend fun startTimeLog(task: Task)

    suspend fun stopTimeLog(task: Task)

    suspend fun getTimeLog(): Flow<List<TimeLog>>

    suspend fun getCurrentUser(): User

    fun getShowCompleteFlag(): Boolean

    fun getCurrentTaskList(user: User):TaskList

    suspend fun getPhotoDescriptions(taskInternalId: UUID): List<PhotoDescriptionUI>

    suspend fun getAudioDescriptions(taskInternalId: UUID): List<AudioDescriptionUI>

    suspend fun addPhotoDescription(photoDescription: PhotoDescriptionUI)

    suspend fun addAudioDescription(audioDescriptionUI: AudioDescriptionUI)

    suspend fun getTask(taskInternalId: String):Task

    suspend fun saveTask(task: Task)

    suspend fun getActiveTaskIdFlow(): Flow<List<TimeLog>>
}