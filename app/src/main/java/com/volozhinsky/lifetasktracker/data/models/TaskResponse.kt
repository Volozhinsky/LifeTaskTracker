package com.volozhinsky.lifetasktracker.data.models

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("selfLink") val selfLink: String? = null,
    @SerializedName("parent") val parent: String? = null,
    @SerializedName("position") val position: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("due") val due: String? = null,
)
