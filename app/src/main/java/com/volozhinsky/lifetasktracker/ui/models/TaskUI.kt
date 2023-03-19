package com.volozhinsky.lifetasktracker.ui.models

import java.time.LocalDateTime

data class TaskUI(
    val id: String,
    val title: String,
    val selfLink: String,
    val parent: String,
    val position: Int,
    val notes: String,
    val status: Boolean,
    val due: LocalDateTime,
)
