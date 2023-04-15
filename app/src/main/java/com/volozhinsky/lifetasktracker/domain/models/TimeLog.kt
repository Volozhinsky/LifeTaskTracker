package com.volozhinsky.lifetasktracker.domain.models

import java.time.LocalDateTime
import java.util.UUID

data class TimeLog(
    val taskInternalId: UUID,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
