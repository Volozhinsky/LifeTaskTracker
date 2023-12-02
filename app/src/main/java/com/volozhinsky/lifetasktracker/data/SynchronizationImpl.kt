package com.volozhinsky.lifetasktracker.data

import com.volozhinsky.lifetasktracker.data.database.TasksDao
import com.volozhinsky.lifetasktracker.data.mappers.AudioDescriptionMapper
import com.volozhinsky.lifetasktracker.data.mappers.PhotoDescriptionMapper
import com.volozhinsky.lifetasktracker.data.mappers.TaskListMapper
import com.volozhinsky.lifetasktracker.data.mappers.TaskMapper
import com.volozhinsky.lifetasktracker.data.mappers.TimeLogMapper
import com.volozhinsky.lifetasktracker.data.models.GetTasksResponse
import com.volozhinsky.lifetasktracker.data.models.TaskListResponse
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.data.network.GoogleTasksApiService
import com.volozhinsky.lifetasktracker.data.pref.QueryProperties
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.repository.Synchronization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class SynchronizationImpl @Inject constructor(
    private val googleTasksApiService: GoogleTasksApiService,
    private val taskListMapper: TaskListMapper,
    private val taskMapper: TaskMapper,
    private val tasksDao: TasksDao,
    private val queryProperties: QueryProperties,
    private val photoDescriptionMapper: PhotoDescriptionMapper,
    private val audioDescriptionMapper: AudioDescriptionMapper,
    private val userDataSource: UserDataSource,
    private val timeLogMapper: TimeLogMapper
) : Synchronization {

    override suspend fun synchronizeAll() {
        synchronizeToGoogle()
        synchronizeToDatabase()
    }

    override suspend fun synchronizeToGoogle() {
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

    override suspend fun synchronizeToDatabase() {
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


    private suspend fun getTaskListsFromApi(): List<TaskListResponse> {
        return withContext(Dispatchers.IO) {
            val response = googleTasksApiService.getList()
            response?.items ?: throw Exception()
        }
    }

    private suspend fun getTasksFromApi(listId: String): List<TaskResponse> {
        val fullTaskList = mutableListOf<TaskResponse>()
        var nextPageResponse: GetTasksResponse?
        return withContext(Dispatchers.IO) {
            val response = googleTasksApiService.getTasks(listId)
            response.let { getTaskResponse ->
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
}