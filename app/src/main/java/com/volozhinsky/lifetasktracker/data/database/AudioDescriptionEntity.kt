package com.volozhinsky.lifetasktracker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "AudioDescription")
data class AudioDescriptionEntity(
    @PrimaryKey @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("taskInternalId") val taskInternalId: UUID,
    @ColumnInfo("recordDate") val recordDate: LocalDateTime
)
