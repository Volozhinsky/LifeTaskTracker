package com.volozhinsky.lifetasktracker.ui.task_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volozhinsky.lifetasktracker.data.pref.QueryProperties
import com.volozhinsky.lifetasktracker.domain.GetTasksUseCase
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import com.volozhinsky.lifetasktracker.ui.mappers.TaskMapperUI
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailTopViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val taskMapperUI: TaskMapperUI,
    private val repository: GoogleTasksRepository,
) : ViewModel() {
    private var _taskLiveData = MutableLiveData<TaskUI>()
    val taskLiveData get() = _taskLiveData
    //val taskUI: TaskUI

    fun getTask(taskInternalId: String) {
        if (taskInternalId.isNotEmpty()) {
            viewModelScope.launch {
                _taskLiveData.value = taskMapperUI.mapDomainToUi(repository.getTask(taskInternalId))
            }
        } else{
            _taskLiveData.value=taskMapperUI.mapDomainToUi(Task())
        }
    }

    fun saveTask() {
        taskLiveData.value?.let {
            viewModelScope.launch {
                repository.saveTask(taskMapperUI.mapUiToDomain(it))
            }
        }
    }
}