package com.volozhinsky.lifetasktracker.domain.models

import java.time.LocalDateTime
import java.util.UUID

class Task(
    val id: String = "",
    val internalId: UUID = UUID.randomUUID(),
    val title: String = "",
    val selfLink: String = "",
    val parent: String = "",
    val position: Int = 0,
    val notes: String = "",
    val status: Boolean = false,
    val due: LocalDateTime = LocalDateTime.MIN,
)