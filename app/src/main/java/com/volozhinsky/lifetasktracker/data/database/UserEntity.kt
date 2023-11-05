package com.volozhinsky.lifetasktracker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "users", primaryKeys = ["account_name"])
data class UserEntity(
    @ColumnInfo("account_name") val account: String
)
