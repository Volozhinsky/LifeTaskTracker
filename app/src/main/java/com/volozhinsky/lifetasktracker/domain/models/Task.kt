package com.volozhinsky.lifetasktracker.domain.models

import com.google.gson.annotations.SerializedName
import java.util.Date

class Task(
    val id: String,
    val title: String,
    val selfLink: String,
    val parent: String,
    val position: Int,
    val notes: String,
    val status: String,
    val due: Date,
)