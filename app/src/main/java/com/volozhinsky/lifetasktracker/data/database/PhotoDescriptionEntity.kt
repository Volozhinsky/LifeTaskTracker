package com.volozhinsky.lifetasktracker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "PhotoDescription")
data class PhotoDescriptionEntity(
    @PrimaryKey @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("taskInternalId") val taskInternalId: UUID,
)
