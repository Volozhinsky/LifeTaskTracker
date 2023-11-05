package com.volozhinsky.lifetasktracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskListEntity::class,TaskEntity::class,PhotoDescriptionEntity::class,AudioDescriptionEntity::class,TimeLogEntity::class,UserEntity::class], version = 1)
@TypeConverters(TaskDataBaseTypeConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getTasksDao(): TasksDao
}