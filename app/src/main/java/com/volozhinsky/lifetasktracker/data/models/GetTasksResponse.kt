package com.volozhinsky.lifetasktracker.data.models

import com.squareup.moshi.Json

data class GetTasksResponse(
    @Json(name ="items") val items: List<TaskResponse>,
    @Json(name ="nextPageToken") val nextPageToken: String? = null
)