package com.volozhinsky.lifetasktracker.data.network

import androidx.room.Insert
import com.volozhinsky.lifetasktracker.data.models.GetTaskListResponse
import com.volozhinsky.lifetasktracker.data.models.GetTasksResponse
import com.volozhinsky.lifetasktracker.data.models.TaskResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleTasksApiService {

    @GET("users/@me/lists")
    suspend fun getList(): GetTaskListResponse

    @GET("lists/{tasklist}/tasks/")
    suspend fun getTasks(@Path( "tasklist" )tasklist: String,@Query("showHidden") showHidden:Boolean): GetTasksResponse

    @GET("lists/{tasklist}/tasks/")
    suspend fun getTasksNextPage(@Path( "tasklist" ) tasklist: String, @Query("pageToken") nextPageToken: String,@Query("showHidden") showHidden:Boolean): GetTasksResponse

    @POST("lists/{tasklist}/tasks/")
    suspend fun insertTask(@Path( "tasklist") tasklist: String, @Body task: TaskResponse):TaskResponse

    @PUT("lists/{tasklist}/tasks/{taskID}")
    suspend fun updateTask(@Path( "tasklist") tasklist: String,@Path( "taskID")taskID: String, @Body task: TaskResponse):TaskResponse
}