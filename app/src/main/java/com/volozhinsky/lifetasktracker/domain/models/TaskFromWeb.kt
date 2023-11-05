package com.volozhinsky.lifetasktracker.domain.models

import com.squareup.moshi.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class TaskFromWeb(
    val id: String,
    val title: String,
    val selfLink: String,
    val parent: String,
    val position: Int,
    val notes: String,
    val status: Boolean,
    val due: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneOffset.UTC)
)
