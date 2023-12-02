package com.volozhinsky.lifetasktracker.domain

import com.volozhinsky.lifetasktracker.domain.repository.Synchronization
import javax.inject.Inject

class SynchronizeTasksUseCase @Inject constructor(
    private val synchronization: Synchronization
) {

    suspend fun synchronizeTaskLists() {
        synchronization.synchronizeAll()
    }
}