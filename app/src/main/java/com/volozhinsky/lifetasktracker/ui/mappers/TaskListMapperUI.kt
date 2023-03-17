package com.volozhinsky.lifetasktracker.ui.mappers

import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.ui.models.TaskListUI
import javax.inject.Inject

class TaskListMapperUI @Inject constructor(){
    fun mapDomainToUi(response: TaskList): TaskListUI = with(response) {
        TaskListUI(
            title = title,
            id = id
        )
    }
}