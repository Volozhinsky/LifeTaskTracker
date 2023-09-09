package com.volozhinsky.lifetasktracker.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.dateDifference
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {

    fun getTasks(showCompleted: Boolean): LiveData<List<Task>> {
        val tasksLiveData = taskListRepository.getTasksFromTaskList()
        val timeLogs = taskListRepository.getTimeLog()
        return if (showCompleted) {
            calculateLog(tasksLiveData, timeLogs)
        } else {
            val filteredTasks = tasksLiveData.map{ tasks ->
                tasks.filter { !it.status }
            }
            calculateLog(filteredTasks, timeLogs)
        }
    }

    private fun calculateLog(
        task: LiveData<List<Task>>,
        timeLog: List<TimeLog>
    ): LiveData<List<Task>> {
        return Transformations.map(task) { taskList ->
            taskList.map { task ->
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
                    logMinutes = calculateMinutes(milliseconds)
                )
            }
        }
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

    private fun calculateMilliseconds(timeLogs: List<TimeLog>): Long {
        var milliseconds = 0L
        timeLogs.forEach { timeLog ->
            milliseconds += timeLog.endDate.dateDifference(timeLog.startDate)
        }
        return milliseconds
    }
}