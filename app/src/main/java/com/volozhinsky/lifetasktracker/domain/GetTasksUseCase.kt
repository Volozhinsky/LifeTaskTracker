package com.volozhinsky.lifetasktracker.domain

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {

    suspend fun getTasks(): List<Task> = taskListRepository.getTasks()
}