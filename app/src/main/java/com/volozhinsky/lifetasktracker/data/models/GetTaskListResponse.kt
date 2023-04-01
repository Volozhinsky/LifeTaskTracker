package com.volozhinsky.lifetasktracker.data.models

import com.squareup.moshi.Json

data class GetTaskListResponse(
    @Json(name ="kind") val kind: String? = null,
    @Json(name ="items") val items: List<TaskListResponse>? = null
)
