package com.volozhinsky.lifetasktracker.data

import com.volozhinsky.lifetasktracker.data.database.TaskListEntity
import com.volozhinsky.lifetasktracker.data.database.TasksDao
import com.volozhinsky.lifetasktracker.data.mappers.TaskListMapper
import com.volozhinsky.lifetasktracker.data.mappers.TaskMapper
import com.volozhinsky.lifetasktracker.data.models.GetTasksResponse
import com.volozhinsky.lifetasktracker.data.models.TaskListResponse
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.data.network.GoogleTasksApiService
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val googleTasksApiService: GoogleTasksApiService,
    private val taskListMapper: TaskListMapper,
    private val taskMapper: TaskMapper,
    private val tasksDao: TasksDao,
    private val userDataSource: UserDataSource
) : LifeTasksRepository, GoogleTasksRepository {

    private val queryProperties = QueryProperties()

    override suspend fun getTaskLists(): List<TaskList> {
        val items = withContext(Dispatchers.IO){ tasksDao.getTaskLists(queryProperties.account)}
        return items.map { taskListMapper.mapEntityToDomain(it) }
    }

    override suspend fun getTasks(): List<Task> {
        val  items = withContext(Dispatchers.IO){
            tasksDao.getTasks(queryProperties.account,queryProperties.taskListId)
        }
        return items.map { taskMapper.mapEntityToDomain(it) }
    }

    override suspend fun getSelectedTaskList(): TaskList {
        val items = withContext(Dispatchers.IO){tasksDao.getSelectedTaskList(queryProperties.account,queryProperties.taskListId)}
        if (items.isNotEmpty()){
            return items.map { taskListMapper.mapEntityToDomain(it) }.first()
        } else{
            return taskListMapper.mapEntityToDomain(TaskListEntity("","","",""))
        }
    }

    override suspend fun synchronizeTaskLists(){
        val taskListsResponse = getTaskListsFromApi()
        withContext(Dispatchers.IO){
            val taskListsEntity = taskListsResponse.map { taskListMapper.mapResponseToEntity(it,queryProperties.account) }
            tasksDao.insertAllIntoTaskLists(*taskListsEntity.toTypedArray())
            taskListsEntity.forEach { taskListsEntity ->
                val tasksResponse = getTasksFromApi(taskListsEntity.id)
                val presentTasks = tasksDao.getTasksByID(queryProperties.account,taskListsEntity.id,tasksResponse.map { it.id ?: ""})
                val tasksEntity = tasksResponse.map {tasksResponse->
                    taskMapper.mapResponseToEntity(tasksResponse,
                        queryProperties.account,
                                                taskListsEntity.id,
                                        presentTasks.find { it.id == tasksResponse.id }?.internalId ?: UUID.randomUUID()) }
                tasksDao.insertAllIntoTask(*tasksEntity.toTypedArray())
            }
        }
    }

    private suspend fun getTasksFromApi(listId: String): List<TaskResponse> {
        val fullTaskList = mutableListOf<TaskResponse>()
        var nextPageResponse: GetTasksResponse?
        return withContext(Dispatchers.IO){
            val response = googleTasksApiService.getTasks(listId)
            response?.let {getTaskResponse ->
                fullTaskList.addAll(getTaskResponse.items)
                var nextPageToken: String = getTaskResponse.nextPageToken ?: ""
                while(nextPageToken.isNotEmpty()){
                    nextPageResponse = googleTasksApiService.getTasksNextPage(listId,nextPageToken)
                    nextPageToken = nextPageResponse?.nextPageToken.orEmpty()
                    nextPageResponse?.let {
                        fullTaskList.addAll(it.items)
                    }
                }
            }
            fullTaskList
        }
    }

    private suspend  fun getTaskListsFromApi(): List<TaskListResponse>{
        return withContext(Dispatchers.IO){
            val response = googleTasksApiService.getList()
                response?.items ?: throw Exception()
        }
    }

    override suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO){
            val taskResponse = googleTasksApiService.insertTask(queryProperties.taskListId, taskMapper.mapDomainToResponseCreate(task))
            tasksDao.insertAllIntoTask(taskMapper.mapResponseToEntity(taskResponse,queryProperties.account,queryProperties.taskListId,task.internalId))
        }
    }

    override suspend fun getTask(taskInternalId: String): Task {
        val task = tasksDao.getTasksByID(queryProperties.account,queryProperties.taskListId, listOf(taskInternalId))
        return taskMapper.mapEntityToDomain(task.first())
    }

    override suspend fun saveTask(task: Task) {
        tasksDao.insertAllIntoTask(taskMapper.mapDomainToEntity(task,queryProperties.account,queryProperties.taskListId))
    }

    private inner class QueryProperties{
        val account get() = userDataSource.getAccountName()
        val taskListId get() = userDataSource.getSelectedTaskListID()
    }


}