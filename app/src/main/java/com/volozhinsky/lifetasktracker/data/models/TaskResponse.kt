package com.volozhinsky.lifetasktracker.data.models

import com.squareup.moshi.Json

data class TaskResponse(
    @Json(name ="id") val id: String? = null,
    @Json(name ="title") val title: String? = null,
    @Json(name ="selfLink") val selfLink: String? = null,
    @Json(name ="parent") val parent: String? = null,
    @Json(name ="position") val position: String? = null,
    @Json(name ="notes") val notes: String? = null,
    @Json(name ="status") val status: String? = null,
    @Json(name ="due") val due: String? = null,
)
