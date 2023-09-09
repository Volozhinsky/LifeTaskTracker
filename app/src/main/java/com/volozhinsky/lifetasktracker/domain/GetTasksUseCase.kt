package com.volozhinsky.lifetasktracker.domain

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.dateDifference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskListRepository: LifeTasksRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getTasks(showCompleted: Boolean): Flow<List<Task>> {
        val tasksLiveFlow = taskListRepository.getTasksFromTaskList(showCompleted)
        return tasksLiveFlow.transformLatest {
            val timeLogsFlow = taskListRepository.getTimeLog()
            timeLogsFlow.collect { timeLogList ->
                val result = if (showCompleted) {
                    calculateLog(tasksLiveFlow, timeLogList)
                } else {
                    val filteredTasks = tasksLiveFlow.map { tasks ->
                        tasks.filter { !it.status }
                    }
                    calculateLog(filteredTasks, timeLogList)
                }
                emit(result)
            }
        }
    }

        private fun calculateLog(
            task: Flow<List<Task>>,
            timeLog: List<TimeLog>
        ): Flow<List<Task>> {
            return task.map { taskList ->
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