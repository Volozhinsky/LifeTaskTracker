package com.volozhinsky.lifetasktracker.ui

import com.volozhinsky.lifetasktracker.domain.models.Task

interface GoogleTasksRepository {

    suspend fun synchronizeTaskLists()

}