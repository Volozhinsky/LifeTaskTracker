package com.volozhinsky.lifetasktracker.ui.tasks_list

import com.volozhinsky.lifetasktracker.ui.models.TaskUI

interface TaskListVHListner {

    fun onItemClick(taskId: String)

    fun onStartTiming(task: TaskUI)
}