package com.volozhinsky.lifetasktracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskListEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getTasksDao(): TasksDao
}