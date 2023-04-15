package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.TimeLogEntity
import com.volozhinsky.lifetasktracker.domain.models.TimeLog
import javax.inject.Inject

class TimeLogMapper @Inject constructor() {

    fun mapEntityToDomain(entity: TimeLogEntity): TimeLog = with(entity){
        TimeLog(
            taskInternalId = internalId,
            startDate = startDate,
            endDate = endDate
        )
    }
}