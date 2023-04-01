package com.volozhinsky.lifetasktracker.ui.mappers

import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import javax.inject.Inject

class TaskMapperUI @Inject constructor() {
    fun mapDomainToUi(response: Task): TaskUI = with(response) {
        TaskUI(
            id = id,
            title = title,
            selfLink = selfLink,
            parent =parent,
            notes = notes,
            status = status,
            due = due,
            position = position
        )
    }
}