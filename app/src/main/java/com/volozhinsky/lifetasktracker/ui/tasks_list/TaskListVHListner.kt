package com.volozhinsky.lifetasktracker.ui.tasks_list

import com.volozhinsky.lifetasktracker.ui.models.TaskUI

interface TaskListVHListner {

    fun onItemClick(task: TaskUI)

    fun onStartTiming(task: TaskUI)

    fun onStatusClick(task: TaskUI)
}