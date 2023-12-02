package com.volozhinsky.lifetasktracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TasksDao {

    @Query("SELECT * " +
            "FROM task_lists " +
            "WHERE account = :account")
    fun getTaskLists(account: String): Flow<List<TaskListEntity>>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account "+
            "AND listId= :taskListId ")
    fun getAllTasksFromTaskList(account: String, taskListId: String): Flow<List<TaskEntity>>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account "+
            "AND listId= :taskListId "+
            "AND NOT status ")
    fun getActiveTasksFromTaskList(account: String, taskListId: String): Flow<List<TaskEntity>>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account " +
            "AND listId= :taskListId " +
            "AND id IN (:ids) ")
    fun getTasksByID(account: String, taskListId: String, ids: List<String>): List<TaskEntity>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account " +
            "AND listId= :taskListId " +
            "AND internalId IN (:ids) ")
    fun getTasksByInternalID(account: String, taskListId: String, ids: List<String>): List<TaskEntity>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account " +
            "AND NOT sinc  ")
    fun getTasksUnsinc(account: String): List<TaskEntity>

    @Query("SELECT * " +
            "FROM tasks " +
            "WHERE account = :account " +
            "AND NOT sinc  ")
    fun getTasksUnsincFlow(account: String): Flow<List<TaskEntity>>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAllIntoTaskLists(vararg taskListEntity: TaskListEntity)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAllIntoTask(vararg taskEntity: TaskEntity)

    @Query("SELECT * FROM PhotoDescription WHERE taskInternalId = (:taskInternalId)")
    fun getPhotoDescriptions(taskInternalId: UUID): List<PhotoDescriptionEntity>
    @Query("SELECT * FROM PhotoDescription WHERE id = (:id)")
    fun getPhotoDescription(id:UUID): PhotoDescriptionEntity

    @Update
    fun updatePhotoDescription(photoDescription:PhotoDescriptionEntity)

    @Insert
    fun addPhotoDescription(photoDescription:PhotoDescriptionEntity)

    @Query("SELECT * FROM AudioDescription WHERE taskInternalId = (:taskInternalId)")
    fun getAudioDescriptions(taskInternalId: UUID): List<AudioDescriptionEntity>

    @Query("SELECT * FROM AudioDescription WHERE id = (:id)")
    fun getAudioDescription(id:UUID): PhotoDescriptionEntity

    @Update
    fun updateAudioDescription(audioDescription:AudioDescriptionEntity)

    @Insert
    fun addAudioDescription(audioDescription:AudioDescriptionEntity)

    @Query("SELECT * FROM TimeLog WHERE listId = (:taskListId)")
    fun getTimeLogs(taskListId: String): Flow<List<TimeLogEntity>>

    @Insert
    fun addTimeLog(timeLog: TimeLogEntity)
}