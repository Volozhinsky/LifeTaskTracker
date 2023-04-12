package com.volozhinsky.lifetasktracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.tasks.TasksScopes
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.data.database.AppDataBase
import com.volozhinsky.lifetasktracker.data.database.TasksDao
import com.volozhinsky.lifetasktracker.data.network.GoogleRequestInterceptor
import com.volozhinsky.lifetasktracker.data.network.GoogleTasksApiService
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.ui.ChooseAccountContract
import com.volozhinsky.lifetasktracker.ui.UserRecoverableAuthContract
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun providePrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideCredential(@ApplicationContext context: Context): GoogleAccountCredential {
        return GoogleAccountCredential.usingOAuth2(
            context,
            Collections.singleton(TasksScopes.TASKS)
        )
    }

    @Provides
    fun provideChooseAccountContract (credential: GoogleAccountCredential): ChooseAccountContract{
        return ChooseAccountContract(credential)
    }

    @Provides
    fun provideUserRecoverableAuthContract (): UserRecoverableAuthContract{
        return UserRecoverableAuthContract()
    }

    @Provides
    fun provideRetrofitTaskList(client: OkHttpClient): Retrofit{
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .baseUrl("https://tasks.googleapis.com/tasks/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }



    @Provides
    fun okhttpClient(credential: GoogleAccountCredential, prefs: UserDataSource): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(GoogleRequestInterceptor(credential, prefs)).build()
    }

    @Provides
    fun provideGoogleTasksApiService(retrofit: Retrofit): GoogleTasksApiService {
        return retrofit.create(GoogleTasksApiService::class.java)
    }

    @Provides
    fun getTaskDao(@ApplicationContext context: Context): TasksDao{
        val db = Room.databaseBuilder(context, AppDataBase::class.java,"main_database")
            .build()
        return db.getTasksDao()
    }

    @Provides
    @Named("api")
    fun provideAPIDateTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofPattern(DATE_FORMAT,Locale.ENGLISH)
    }

    @Provides
    @Named("ui")
    fun provideUIDateTimeFormatter(@ApplicationContext context: Context): DateTimeFormatter {
        return DateTimeFormatter.ofPattern(context.getString(R.string.dateFormat),Locale.ENGLISH)
    }

    @Provides
    @Named("filesDir")
    fun provideFilesDir(@ApplicationContext context: Context): File{
        return context.filesDir
    }

    private const val PREFS_KEY = "prefs_key"
    private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
}