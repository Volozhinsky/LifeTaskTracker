package com.volozhinsky.lifetasktracker.ui

interface GoogleTasksRepository {

    suspend fun synchronizeTaskLists()
}