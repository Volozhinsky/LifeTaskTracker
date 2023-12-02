package com.volozhinsky.lifetasktracker.data

import com.volozhinsky.lifetasktracker.data.database.TasksDao
import com.volozhinsky.lifetasktracker.data.database.TimeLogEntity
import com.volozhinsky.lifetasktracker.data.mappers.*
import com.volozhinsky.lifetasktracker.data.models.GetTasksResponse
import com.volozhinsky.lifetasktracker.data.models.TaskListResponse
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.data.network.GoogleTasksApiService
import com.volozhinsky.lifetasktracker.data.pref.QueryProperties
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskFromWeb
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import com.volozhinsky.lifetasktracker.domain.models.User
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.DescriptionsRepository
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val googleTasksApiService: GoogleTasksApiService,
    private val taskListMapper: TaskListMapper,
    private val taskMapper: TaskMapper,
    private val tasksDao: TasksDao,
    private val queryProperties: QueryProperties,
    private val photoDescriptionMapper: PhotoDescriptionMapper,
    private val audioDescriptionMapper: AudioDescriptionMapper,
    private val userDataSource: UserDataSource,
    private val timeLogMapper: TimeLogMapper
) : LifeTasksRepository, DescriptionsRepository {

    override suspend fun getTaskLists(user: User): Flow<List<TaskList>> {
        return withContext(Dispatchers.IO) {
            val itemsLiveData = tasksDao.getTaskLists(user.accountName)
            itemsLiveData.map { taskList ->
                taskList.map { taskListMapper.mapEntityToDomain(it, user) }
            }
        }
    }

    override suspend fun getTasksFromTaskList(
        showComplete: Boolean,
        taskList: TaskList
    ): Flow<List<Task>> {

        val items = if (showComplete)
            tasksDao.getAllTasksFromTaskList(queryProperties.account, taskList.id)
        else tasksDao.getActiveTasksFromTaskList(
            queryProperties.account,
            taskList.id
        )
        return items.map { taskListEntity ->
            taskListEntity.map { taskMapper.mapEntityToDomain(it) }
        }
    }

    override suspend fun getCurrentUser(): User {
        return User(queryProperties.account)
    }

    override fun getShowCompleteFlag(): Boolean {
        return queryProperties.showComplete
    }

    override fun getCurrentTaskList(user: User): TaskList {
        return TaskList("todo", queryProperties.taskListId, user)
        //TODO here must be real tasklist
    }

    override suspend fun getTasksUnsinc(user: User): Flow<List<Task>> {
        val tasksFlow = tasksDao.getTasksUnsincFlow(queryProperties.account)
        return tasksFlow.map {taskEntity -> taskEntity.map { taskMapper.mapEntityToDomain(it) }}
        //TODO different users
    }

    override suspend fun insertAllIntoTask(tasks: List<Task>,taskList: TaskList) {
        val tasksEntity = tasks.map { taskMapper.mapDomainToEntity(it,queryProperties.account,taskList.id) }
        tasksDao.insertAllIntoTask(*tasksEntity.toTypedArray())
    }

    suspend fun getTask(taskInternalId: String): Task {
        return withContext(Dispatchers.IO) {
            val task = tasksDao.getTasksByInternalID(
                queryProperties.account,
                queryProperties.taskListId,
                listOf(taskInternalId)
            )
            taskMapper.mapEntityToDomain(task.first())
        }
    }

    suspend fun saveTask(task: Task) {
        withContext(Dispatchers.IO) {
            tasksDao.insertAllIntoTask(
                taskMapper.mapDomainToEntity(
                    task,
                    queryProperties.account,
                    queryProperties.taskListId
                )
            )
        }
    }

    override suspend fun getPhotoDescriptions(taskInternalId: UUID): List<PhotoDescriptionUI> {
        return withContext(Dispatchers.IO) {
            tasksDao.getPhotoDescriptions(taskInternalId)
                .map { photoDescriptionMapper.mapEntityToUI(it) }
        }
    }

    override suspend fun getAudioDescriptions(taskInternalId: UUID): List<AudioDescriptionUI> {
        return withContext(Dispatchers.IO) {
            tasksDao.getAudioDescriptions(taskInternalId)
                .map { audioDescriptionMapper.mapEntityToUI(it) }
        }
    }

    override suspend fun addPhotoDescription(photoDescription: PhotoDescriptionUI) {
        return withContext(Dispatchers.IO) {
            tasksDao.addPhotoDescription(photoDescriptionMapper.mapUIToEntity(photoDescription))
        }
    }

    override suspend fun addAudioDescription(audioDescriptionUI: AudioDescriptionUI) {
        withContext(Dispatchers.IO) {
            tasksDao.addAudioDescription(
                (audioDescriptionMapper.mapUIToEntity(
                    audioDescriptionUI,
                    LocalDateTime.now()
                ))
            )
        }
    }

    override suspend fun startTimeLog(task: Task) {
        saveCurrent()
        startNew(task)
    }

    private suspend fun saveCurrent() {
        val currentTaskId = userDataSource.getCurrentTaskId()
        val currentStartDate = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(userDataSource.getCurrentTaskStartDate()),
            ZoneOffset.UTC
        )
        if (currentTaskId.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                tasksDao.addTimeLog(
                    TimeLogEntity(
                        id = UUID.randomUUID().toString(),
                        internalId = UUID.fromString(currentTaskId),
                        startDate = currentStartDate,
                        endDate = LocalDateTime.now(),
                        listId = queryProperties.taskListId
                    )
                )
            }
        }
    }

    private fun startNew(task: Task) {
        userDataSource.setCurrentTaskID(task.internalId.toString())
        userDataSource.setCurrentTaskStartDate(
            LocalDateTime.now()
                .atZone(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        )
    }

    override suspend fun getTimeLog(): Flow<List<TimeLog>> {
        val timeLogFlow = tasksDao.getTimeLogs(queryProperties.taskListId)
        return timeLogFlow.map { timeLog ->
            timeLog.map { timeLogMapper.mapEntityToDomain(it) }
        }
    }

    override suspend fun stopTimeLog() {
        saveCurrent()
        userDataSource.setCurrentTaskID("")
    }
}