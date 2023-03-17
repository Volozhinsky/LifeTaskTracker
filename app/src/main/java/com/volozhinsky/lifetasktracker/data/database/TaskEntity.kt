package com.volozhinsky.lifetasktracker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tasks", primaryKeys = ["listId","id","account"])
data class TaskEntity(
     @ColumnInfo("listId") val listId: String,
     @ColumnInfo("account") val account: String,

    @ColumnInfo("id") val id: String,
    @ColumnInfo("title")val title: String,
    @ColumnInfo("selfLink")val selfLink: String,
    @ColumnInfo("parent")val parent: String,
    @ColumnInfo("position")val position: Int,
    @ColumnInfo("notes")val notes: String,
    @ColumnInfo("status")val status: String,
    @ColumnInfo("due")val due: Date
)