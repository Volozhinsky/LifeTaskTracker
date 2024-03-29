package com.volozhinsky.lifetasktracker.ui.mappers

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import javax.inject.Inject

class TaskMapperUI @Inject constructor() {
    fun mapDomainToUi(response: Task): TaskUI = with(response) {
        TaskUI(
            id = id,
            internalId = internalId,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = status,
            due = due,
            position = position,
            logDays = logDays,
            logHours = logHours,
            logMinutes = logMinutes,
            activeTask = activeTask
        )
    }

    fun mapUiToDomain(response: TaskUI): Task = with(response) {
        Task(
            id = id,
            internalId = internalId,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = status,
            due = due,
            position = position,
            activeTask = activeTask
        )
    }
}