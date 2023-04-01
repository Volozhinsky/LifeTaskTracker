package com.volozhinsky.lifetasktracker.domain.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

class Task(
    val id: UUID,
    val title: String,
    val selfLink: String,
    val parent: String,
    val position: Int,
    val notes: String,
    val status: Boolean,
    val due: LocalDateTime,
)