package com.volozhinsky.lifetasktracker.di

import com.volozhinsky.lifetasktracker.data.TasksRepositoryImpl
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryDataModule {

    @Binds
    abstract fun getTasksRepository(impl: TasksRepositoryImpl): LifeTasksRepository

    @Binds
    abstract fun getGoogleTasksRepository(impl: TasksRepositoryImpl): GoogleTasksRepository
}