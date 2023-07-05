package com.volozhinsky.lifetasktracker.domain

import androidx.lifecycle.LiveData
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import javax.inject.Inject

class GetTasksListUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {
    fun getTaskLists(): LiveData<List<TaskList>> = taskListRepository.getTaskLists()
}