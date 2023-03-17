package com.volozhinsky.lifetasktracker.data.models

import com.google.gson.annotations.SerializedName

data class GetTasksResponse(
    @SerializedName("items") val items: List<TaskResponse>,
    @SerializedName("nextPageToken") val nextPageToken: String
)