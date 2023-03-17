package com.volozhinsky.lifetasktracker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_lists")
data class TaskListEntity(
    @PrimaryKey @ColumnInfo("id") val id: String,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("selfLink") val selfLink: String,
    @ColumnInfo("account") val account: String
)
