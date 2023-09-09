package com.volozhinsky.lifetasktracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
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
) : LifeTasksRepository, GoogleTasksRepository, DescriptionsRepository {

    override suspend fun getTaskLists(): Flow<List<TaskList>> {
        return withContext(Dispatchers.IO){
            val itemsLiveData = tasksDao.getTaskLists(queryProperties.account)
            itemsLiveData.map { taskList ->
                taskList.map { taskListMapper.mapEntityToDomain(it) }
            }
        }
    }

    override fun getTasksFromTaskList(showComplete: Boolean): Flow<List<Task>> {
        val items = if (showComplete)
            tasksDao.getAllTasksFromTaskList(queryProperties.account, queryProperties.taskListId)
        else tasksDao.getActiveTasksFromTaskList(
            queryProperties.account,
            queryProperties.taskListId
        )
        return items.map{ taskListEntity ->
            taskListEntity.map { taskMapper.mapEntityToDomain(it) }
        }
    }


    override suspend fun synchronizeTaskLists() {
        synchronizeToGoogle()
        synchronizeToDatabase()
    }

    suspend fun synchronizeToGoogle() {
        withContext(Dispatchers.IO) {
            val unsincTask = tasksDao.getTasksUnsinc(queryProperties.account)
            unsincTask.forEach { taskEntity ->
                val taskResponse = if (taskEntity.id.isNotEmpty()) {
                    googleTasksApiService.updateTask(
                        taskEntity.listId,
                        taskEntity.id,
                        taskMapper.mapEntityToResponse(taskEntity)
                    )
                } else {
                    googleTasksApiService.insertTask(
                        taskEntity.listId,
                        taskMapper.mapEntityToResponseCreate(taskEntity)
                    )
                }
                tasksDao.insertAllIntoTask(
                    taskMapper.mapResponseToEntity(
                        taskResponse,
                        queryProperties.account,
                        taskEntity.listId,
                        taskEntity.internalId
                    )
                )
            }
        }
    }

    suspend fun synchronizeToDatabase() {
        val taskListsResponse = getTaskListsFromApi()
        withContext(Dispatchers.IO) {
            val taskListsEntity = taskListsResponse.map {
                taskListMapper.mapResponseToEntity(
                    it,
                    queryProperties.account
                )
            }
            tasksDao.insertAllIntoTaskLists(*taskListsEntity.toTypedArray())
            taskListsEntity.forEach { taskListsEntity ->
                val tasksResponse = getTasksFromApi(taskListsEntity.id)
                val presentTasks = tasksDao.getTasksByID(
                    queryProperties.account,
                    taskListsEntity.id,
                    tasksResponse.map { it.id ?: "" })
                val tasksEntity = tasksResponse.map { tasksResponse ->
                    taskMapper.mapResponseToEntity(
                        tasksResponse,
                        queryProperties.account,
                        taskListsEntity.id,
                        presentTasks.find { it.id == tasksResponse.id }?.internalId
                            ?: UUID.randomUUID()
                    )
                }
                tasksDao.insertAllIntoTask(*tasksEntity.toTypedArray())
            }
        }
    }

    private suspend fun getTasksFromApi(listId: String): List<TaskResponse> {
        val fullTaskList = mutableListOf<TaskResponse>()
        var nextPageResponse: GetTasksResponse?
        return withContext(Dispatchers.IO) {
            val response = googleTasksApiService.getTasks(listId)
            response?.let { getTaskResponse ->
                fullTaskList.addAll(getTaskResponse.items)
                var nextPageToken: String = getTaskResponse.nextPageToken ?: ""
                while (nextPageToken.isNotEmpty()) {
                    nextPageResponse = googleTasksApiService.getTasksNextPage(listId, nextPageToken)
                    nextPageToken = nextPageResponse?.nextPageToken.orEmpty()
                    nextPageResponse?.let {
                        fullTaskList.addAll(it.items)
                    }
                }
            }
            fullTaskList
        }
    }

    private suspend fun getTaskListsFromApi(): List<TaskListResponse> {
        return withContext(Dispatchers.IO) {
            val response = googleTasksApiService.getList()
            response?.items ?: throw Exception()
        }
    }


    override suspend fun getTask(taskInternalId: String): Task {
        return withContext(Dispatchers.IO) {
            val task = tasksDao.getTasksByInternalID(
                queryProperties.account,
                queryProperties.taskListId,
                listOf(taskInternalId)
            )
            taskMapper.mapEntityToDomain(task.first())
        }
    }

    override suspend fun saveTask(task: Task) {
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

    override fun getTimeLog(): Flow<List<TimeLog>> {
        val timeLogFlow = tasksDao.getTameLogs(queryProperties.taskListId)
        return timeLogFlow.map { timeLog ->
            timeLog.map { timeLogMapper.mapEntityToDomain(it) }
        }
    }

    override suspend fun stopTimeLog() {
        saveCurrent()
        userDataSource.setCurrentTaskID("")
    }
}