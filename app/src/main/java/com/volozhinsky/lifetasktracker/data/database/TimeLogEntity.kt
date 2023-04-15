package com.volozhinsky.lifetasktracker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "TimeLog", primaryKeys = ["id","internalId"])
data class TimeLogEntity(
    @ColumnInfo("id") val id: String,
    @ColumnInfo("internalId") val internalId: UUID,
    @ColumnInfo("listId") val listId: String,
    @ColumnInfo("startDate") val startDate: LocalDateTime,
    @ColumnInfo("endDate") val endDate: LocalDateTime,
)
