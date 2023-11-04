package com.volozhinsky.lifetasktracker.domain

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import javax.inject.Inject

class TimeLogUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
){

    suspend fun startLog(task: Task) = taskListRepository.startTimeLog(task)

    suspend fun stopLog() = taskListRepository.stopTimeLog()
}