package com.volozhinsky.lifetasktracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.api.services.tasks.model.TaskList

@Dao
interface TasksDao {

    @Query("SELECT * " +
            "FROM task_lists " +
            "WHERE account = :account")
    fun getTaskLists(account: String): List<TaskListEntity>

    @Query("SELECT * " +
            "FROM task_lists " +
            "WHERE account = :account "+
            "AND id= :id ")
    fun getSelectedTaskList(account: String, id: String): List<TaskListEntity>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAllIntoTaskLists(vararg taskListEntity: TaskListEntity)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAllIntoTask(vararg taskEntity: TaskEntity)
}