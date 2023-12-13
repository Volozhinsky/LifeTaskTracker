package com.volozhinsky.lifetasktracker.domain.maincontrol

import com.volozhinsky.lifetasktracker.domain.GetTasksListUseCase
import com.volozhinsky.lifetasktracker.domain.GetTasksUseCase
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.domain.models.TaskList
import com.volozhinsky.lifetasktracker.domain.repository.LifeTasksRepository
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class LifeTaskAppControl @Inject constructor(
    private val getTasksListUseCase: GetTasksListUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val lifeTasksRepository: LifeTasksRepository
) {

    suspend fun getTaskListsFlow(): Flow<List<TaskList>> {
        val currentUser = lifeTasksRepository.getCurrentUser()
        return lifeTasksRepository.getTaskLists(currentUser)
    }

    suspend fun getTasksFlow(): Flow<List<Task>> {
        val showComplete = lifeTasksRepository.getShowCompleteFlag()
        val user = lifeTasksRepository.getCurrentUser()
        val currentTaskList = lifeTasksRepository.getCurrentTaskList(user)
        return getTasksUseCase.getTasks(showComplete, currentTaskList)
    }

    suspend fun getPhotoDescriptions(taskInternalId: UUID): List<PhotoDescriptionUI> = lifeTasksRepository.getPhotoDescriptions(taskInternalId)

    suspend fun getAudioDescriptions(taskInternalId: UUID): List<AudioDescriptionUI> = lifeTasksRepository.getAudioDescriptions(taskInternalId)

    suspend fun addPhotoDescription(photoDescription: PhotoDescriptionUI) = lifeTasksRepository.addPhotoDescription(photoDescription)

    suspend fun addAudioDescription(audioDescriptionUI: AudioDescriptionUI) = lifeTasksRepository.addAudioDescription(audioDescriptionUI)

    suspend fun getTask(taskInternalId: String):Task = lifeTasksRepository.getTask(taskInternalId)
    suspend fun saveTask(task: Task) = lifeTasksRepository.saveTask(task)
}