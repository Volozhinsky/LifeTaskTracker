package com.volozhinsky.lifetasktracker.domain.maincontrol

import com.volozhinsky.lifetasktracker.domain.GetTasksListUseCase
import com.volozhinsky.lifetasktracker.domain.GetTasksUseCase
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LifeTaskAppControl @Inject constructor(
    private val getTasksListUseCase: GetTasksListUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val lifeTasksRepository: LifeTasksRepository) {

    suspend fun getTaskListsFlow() :Flow<List<TaskList>> {
        val currentUser = lifeTasksRepository.getCurrentUser()
        return lifeTasksRepository.getTaskLists(currentUser)
    }

    suspend fun getTasksFlow(): Flow<List<Task>>{
        val showComplete = lifeTasksRepository.getShowCompleteFlag()
        val user = lifeTasksRepository.getCurrentUser()
        val currentTaskList = lifeTasksRepository.getCurrentTaskList(user)
        return getTasksUseCase.getTasks(showComplete,currentTaskList)
    }

}