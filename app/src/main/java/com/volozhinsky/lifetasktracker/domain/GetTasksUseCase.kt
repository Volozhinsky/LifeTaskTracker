package com.volozhinsky.lifetasktracker.domain

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.dateDifference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineLatest
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {

    suspend fun getTasks(showCompleted: Boolean,taskList: TaskList): Flow<List<Task>> {
        val tasksLiveFlow = taskListRepository.getTasksFromTaskList(showCompleted,taskList)
        val timeLogsFlow = taskListRepository.getTimeLog()
        val activeTaskIdFlow = taskListRepository.getActiveTaskIdFlow()
        val taskAndTimeLogFlow = tasksLiveFlow.combine(timeLogsFlow) { tasks, logs ->
            calculateLog(tasks, logs)
        }
        val resultFlow =  taskAndTimeLogFlow.combine(activeTaskIdFlow){tasks,activeTasksLog ->
            tasks.map {task ->

                task.activeTask = activeTasksLog.find { it.taskInternalId == task.internalId } != null
            }
            tasks
        }
        return resultFlow
    }

    private fun calculateLog(
        tasks: List<Task>,
        timeLog: List<TimeLog>
    ): List<Task> {
        return tasks.map { task ->
            val filteredTimeLog = timeLog.filter { it.taskInternalId == task.internalId }
            val milliseconds = calculateMilliseconds(filteredTimeLog)
            Task(
                id = task.id,
                internalId = task.internalId,
                title = task.title,
                selfLink = task.selfLink,
                parent = task.parent,
                notes = task.notes,
                status = task.status,
                due = task.due,
                position = task.position,
                logDays = calculateDays(milliseconds),
                logHours = calculateHours(milliseconds),
                logMinutes = calculateMinutes(milliseconds),
                activeTask = false
            )
        }.orEmpty()
    }


    private fun calculateDays(milliseconds: Long): Int {
        return (milliseconds / 86400000).toInt()
    }

    private fun calculateHours(milliseconds: Long): Int {
        return ((milliseconds % 86400000) / 3600000).toInt()
    }

    private fun calculateMinutes(milliseconds: Long): Int {
        return (((milliseconds % 86400000) % 3600000) / 60000).toInt()
    }

    private fun calculateMilliseconds(timeLogs: List<TimeLog>?): Long {
        var milliseconds = 0L
        timeLogs?.forEach { timeLog ->
            milliseconds += timeLog.endDate.dateDifference(timeLog.startDate)
        }
        return milliseconds
    }
}