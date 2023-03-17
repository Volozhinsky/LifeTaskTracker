package com.volozhinsky.lifetasktracker.domain

import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import javax.inject.Inject

class GetTasksListUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {
    suspend fun getTaskLists(): List<TaskList> = taskListRepository.getTaskLists()

    suspend fun getSelectedTaskList(): TaskList = taskListRepository.getSelectedTaskList()
}