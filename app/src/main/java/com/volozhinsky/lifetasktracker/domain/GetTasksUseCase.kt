package com.volozhinsky.lifetasktracker.domain

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {

    suspend fun getTasks(showCompleted: Boolean): List<Task>{
        return if(showCompleted){
             taskListRepository.getTasks()
        } else{
            taskListRepository.getTasks().filter { !it.status }
        }
    }
}