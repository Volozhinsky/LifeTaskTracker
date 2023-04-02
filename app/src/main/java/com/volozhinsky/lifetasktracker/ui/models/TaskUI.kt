package com.volozhinsky.lifetasktracker.ui.models

import java.time.LocalDateTime
import java.util.UUID

data class TaskUI(
    val id: String,
    val internalId: UUID,
    val title: String,
    val selfLink: String,
    val parent: String,
    val position: Int,
    val notes: String,
    val status: Boolean,
    val due: LocalDateTime,
)
