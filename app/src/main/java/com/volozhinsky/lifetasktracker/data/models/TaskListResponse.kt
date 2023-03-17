package com.volozhinsky.lifetasktracker.data.models

import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("title") val title: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("selfLink") val selfLink: String? = null
)
