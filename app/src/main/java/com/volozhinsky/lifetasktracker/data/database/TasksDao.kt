package com.volozhinsky.lifetasktracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account "+
            "AND listId= :taskListId ")
    fun getTasks(account: String, taskListId: String): List<TaskEntity>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account " +
            "AND listId= :taskListId " +
            "AND internalId IN (:ids) ")
    fun getTasksByID(account: String, taskListId: String, ids: List<String>): List<TaskEntity>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account " +
            "AND NOT sinc  ")
    fun getTasksUnsinc(account: String): List<TaskEntity>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAllIntoTaskLists(vararg taskListEntity: TaskListEntity)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAllIntoTask(vararg taskEntity: TaskEntity)
}