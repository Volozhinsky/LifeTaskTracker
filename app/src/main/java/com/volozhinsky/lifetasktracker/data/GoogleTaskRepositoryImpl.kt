package com.volozhinsky.lifetasktracker.data

import com.volozhinsky.lifetasktracker.data.mappers.TaskListMapper
import com.volozhinsky.lifetasktracker.data.mappers.TaskMapper
import com.volozhinsky.lifetasktracker.data.models.GetTasksResponse
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import com.volozhinsky.lifetasktracker.data.network.GoogleTasksApiService
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskFromWeb
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.User
import com.volozhinsky.lifetasktracker.domain.repository.TasksFromWebRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class GoogleTaskRepositoryImpl @Inject constructor(
    private val googleTasksApiService: GoogleTasksApiService,
    private val taskListMapper: TaskListMapper,
    private val taskMapper: TaskMapper
) : TasksFromWebRepository {

    override suspend fun getTaskLists(user: User): List<TaskList> {
        val taskListResponse = withContext(Dispatchers.IO) {
            val response = googleTasksApiService.getList()
            response?.items ?: throw Exception()
        }
        return taskListResponse.map { taskListMapper.mapResponseToDomain(it,user)}
    }

    override suspend fun getTasks(taskList: TaskList): List<TaskFromWeb> {
        val fullTaskList = mutableListOf<TaskResponse>()
        var nextPageResponse: GetTasksResponse?
        return withContext(Dispatchers.IO) {
            val response = googleTasksApiService.getTasks(taskList.id)
            response.let { getTaskResponse ->
                fullTaskList.addAll(getTaskResponse.items)
                var nextPageToken: String = getTaskResponse.nextPageToken ?: ""
                while (nextPageToken.isNotEmpty()) {
                    nextPageResponse = googleTasksApiService.getTasksNextPage(taskList.id, nextPageToken)
                    nextPageToken = nextPageResponse?.nextPageToken.orEmpty()
                    nextPageResponse?.let {
                        fullTaskList.addAll(it.items)
                    }
                }
            }
            fullTaskList.map { taskMapper.mapResponseToDomain(it) }
        }

    }

    override suspend fun addTask(task: Task, taskList: TaskList): Task {
        TODO("Not yet implemented")
    }
}