package com.volozhinsky.lifetasktracker.data.network

import com.volozhinsky.lifetasktracker.data.models.GetTaskListResponse
import com.volozhinsky.lifetasktracker.data.models.GetTasksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GoogleTasksApiService {

    @GET("users/@me/lists")
    fun getList(): Call<GetTaskListResponse>

    @GET("")
    fun getTasks(): Call<GetTasksResponse>
}