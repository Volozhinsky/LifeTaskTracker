package com.volozhinsky.lifetasktracker.data.models

import com.squareup.moshi.Json

data class TaskListResponse(
    @Json(name ="title") val title: String? = null,
    @Json(name ="id") val id: String? = null,
    @Json(name ="selfLink") val selfLink: String? = null
)
