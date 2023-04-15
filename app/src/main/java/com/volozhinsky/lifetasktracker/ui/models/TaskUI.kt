package com.volozhinsky.lifetasktracker.ui.models

import java.time.LocalDateTime
import java.util.UUID

data class TaskUI(
    val id: String,
    val internalId: UUID,
    var title: String,
    val selfLink: String,
    val parent: String,
    val position: Int,
    var notes: String,
    var status: Boolean,
    var due: LocalDateTime,
    val logDays: Int,
    val logHours: Int,
    val logMinutes: Int,
    val activeTask: Boolean
)
