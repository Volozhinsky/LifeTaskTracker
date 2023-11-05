package com.volozhinsky.lifetasktracker.domain

import androidx.lifecycle.LiveData
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.User
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksListUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {
    suspend fun getTaskLists(user: User): Flow<List<TaskList>> = taskListRepository.getTaskLists(user)
}