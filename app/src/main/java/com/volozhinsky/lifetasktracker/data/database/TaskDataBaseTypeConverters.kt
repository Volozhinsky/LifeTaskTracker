package com.volozhinsky.lifetasktracker.data.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.util.*

class TaskDataBaseTypeConverters {
    @TypeConverter
    fun fromDate(date: LocalDateTime): Long? {
        return date?.getLong(ChronoField.CLOCK_HOUR_OF_DAY)
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): LocalDateTime? {
        return millisSinceEpoch?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUIDnullable(uuid: String?): UUID? {
        return uuid?.let { UUID.fromString(uuid) }
    }
}