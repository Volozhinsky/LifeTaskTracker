package com.volozhinsky.lifetasktracker.ui.utils

import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.formatEmptyString
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object UtilsLocalDateTime {

    fun LocalDateTime.formatEmptyString(formatter: DateTimeFormatter, emptyDateString: String): String {
        return if (this.atZone(ZoneOffset.UTC).toInstant().toEpochMilli() == 0L) emptyDateString
        else this.format(formatter)
    }

    fun LocalDateTime.dateDifference(date: LocalDateTime):Long{
        return this.atZone(ZoneOffset.UTC).toInstant().toEpochMilli() -
                date.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
}