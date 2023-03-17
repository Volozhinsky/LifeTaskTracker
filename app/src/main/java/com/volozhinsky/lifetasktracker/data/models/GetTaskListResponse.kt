package com.volozhinsky.lifetasktracker.data.models

import com.google.gson.annotations.SerializedName

data class GetTaskListResponse(
    @SerializedName("kind") val kind: String? = null,
    @SerializedName("items") val items: List<TaskListResponse>? = null
)
