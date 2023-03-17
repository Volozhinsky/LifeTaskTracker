package com.volozhinsky.lifetasktracker.data

import com.volozhinsky.lifetasktracker.data.database.TaskListEntity
import com.volozhinsky.lifetasktracker.data.database.TasksDao
import com.volozhinsky.lifetasktracker.data.mappers.TaskListMapper
import com.volozhinsky.lifetasktracker.data.models.TaskListResponse
import com.volozhinsky.lifetasktracker.data.network.GoogleTasksApiService
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val googleTasksApiService: GoogleTasksApiService,
    private val taskListMapper: TaskListMapper,
    private val tasksDao: TasksDao,
    private val userDataSource: UserDataSource
) : LifeTasksRepository, GoogleTasksRepository {

    override suspend fun getTaskLists(): List<TaskList> {
        val account = userDataSource.getAccountName()
        val items = withContext(Dispatchers.IO){ tasksDao.getTaskLists(account)}
        return items.map { taskListMapper.mapEntityToDomain(it) }
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
        }
    }

    private suspend  fun getTaskListsFromApi(): List<TaskListResponse>{
        return withContext(Dispatchers.IO){
            googleTasksApiService.getList().execute().body()?.items ?: throw Exception()
        }
    }
}