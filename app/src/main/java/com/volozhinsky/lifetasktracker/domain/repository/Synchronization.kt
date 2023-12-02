package com.volozhinsky.lifetasktracker.domain.repository

interface Synchronization {
    suspend fun synchronizeAll()

    suspend fun synchronizeToGoogle ()

    suspend fun synchronizeToDatabase()
}