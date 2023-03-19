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
import retrofit2.Call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val googleTasksApiService: GoogleTasksApiService,
    private val taskListMapper: TaskListMapper,
    private val taskMapper: TaskMapper,
    private val tasksDao: TasksDao,
    private val userDataSource: UserDataSource
) : LifeTasksRepository, GoogleTasksRepository {

    override suspend fun getTaskLists(): List<TaskList> {
        val account = userDataSource.getAccountName()
        val items = withContext(Dispatchers.IO){ tasksDao.getTaskLists(account)}
        return items.map { taskListMapper.mapEntityToDomain(it) }
    }

    override suspend fun getTasks(): List<Task> {
        val account = userDataSource.getAccountName()
        val taskListId = userDataSource.getSelectedTaskListID()
        val  items = withContext(Dispatchers.IO){
            tasksDao.getTasks(account,taskListId)
        }
        return items.map { taskMapper.mapEntityToDomain(it) }
    }

    override suspend fun getSelectedTaskList(): TaskList {
        val account = userDataSource.getAccountName()
        val selectedListId = userDataSource.getSelectedTaskListID()
        val items = withContext(Dispatchers.IO){tasksDao.getSelectedTaskList(account,selectedListId)}
        if (items.isNotEmpty()){
            return items.map { taskListMapper.mapEntityToDomain(it) }.first()
        } else{
            return taskListMapper.mapEntityToDomain(TaskListEntity("","","",""))
        }
    }

    override suspend fun synchronizeTaskLists(){
        val taskListsResponse = getTaskListsFromApi()
        withContext(Dispatchers.IO){
            val account = userDataSource.getAccountName()
            val taskListsEntity = taskListsResponse.map { taskListMapper.mapResponseToEntity(it,account) }
            tasksDao.insertAllIntoTaskLists(*taskListsEntity.toTypedArray())
            taskListsEntity.forEach { taskListsEntity ->
                val tasksResponse = getTasksFromApi(taskListsEntity.id)
                val tasksEntity = tasksResponse.map { taskMapper.mapResponseToEntity(it,account,taskListsEntity.id) }
                tasksDao.insertAllIntoTask(*tasksEntity.toTypedArray())
            }
        }
    }

    private suspend fun getTasksFromApi(listId: String): List<TaskResponse> {
        val fullTaskList = mutableListOf<TaskResponse>()
        var nextPageResponse: GetTasksResponse?
        return withContext(Dispatchers.IO){
            val response = googleTasksApiService.getTasks(listId).execute().body()
            response?.let {getTaskResponse ->
                fullTaskList.addAll(getTaskResponse.items)
                var nextPageToken: String = getTaskResponse.nextPageToken ?: ""
                while(nextPageToken.isNotEmpty()){
                    nextPageResponse = googleTasksApiService.getTasksNextPage(listId,nextPageToken).execute().body()
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
            val response = googleTasksApiService.getList().execute().body()
                response?.items ?: throw Exception()
        }
    }
}