package com.volozhinsky.lifetasktracker.ui.task_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volozhinsky.lifetasktracker.data.pref.QueryProperties
import com.volozhinsky.lifetasktracker.domain.GetTasksUseCase
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.ui.DescriptionsRepository
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import com.volozhinsky.lifetasktracker.ui.mappers.TaskMapperUI
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.TaskListUI
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TaskDetailTopViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val taskMapperUI: TaskMapperUI,
    private val repository: GoogleTasksRepository,
    @Named("ui") val formatter: DateTimeFormatter,
    private val descriptionsRepository: DescriptionsRepository,
    @Named("filesDir") private val filesDir: File
) : ViewModel() {

    private var _taskLiveData = MutableLiveData<TaskUI>()
    val taskLiveData get() = _taskLiveData
    private var _photoDescriptionList = MutableLiveData<List<PhotoDescriptionUI>>()
    val photoDescriptionList get() = _photoDescriptionList
    var newPhotoDescriptionUI: PhotoDescriptionUI? = null
    private var _audioDescriptionList = MutableLiveData<List<AudioDescriptionUI>>()
    val audioDescriptionList get() = _audioDescriptionList
    var recordingInProgress = false
    var plaingInProgress = false
    val taskLiveDataObserver = Observer<TaskUI>(){task ->
        viewModelScope.launch {
            _photoDescriptionList.value =
                descriptionsRepository.getPhotoDescriptions(task.internalId)
        }
        viewModelScope.launch {
            _audioDescriptionList.value =
                descriptionsRepository.getAudioDescriptions(task.internalId)
        }
    }


    fun getTask(taskInternalId: String) {
        if (taskInternalId.isNotEmpty()) {
            viewModelScope.launch {
                _taskLiveData.value = taskMapperUI.mapDomainToUi(repository.getTask(taskInternalId))
            }
        } else {
            _taskLiveData.value = taskMapperUI.mapDomainToUi(Task())
        }
    }

    fun saveTask() {
        taskLiveData.value?.let { taskUI ->
            viewModelScope.launch {
                repository.saveTask(taskMapperUI.mapUiToDomain(taskUI))
            }
        }
    }

    fun createNewPhotoFile(): File? {
        return taskLiveData.value?.let { task ->
            newPhotoDescriptionUI = PhotoDescriptionUI(taskInternalId = task.internalId)
            newPhotoDescriptionUI?.getPhotoFile(filesDir)
        }
    }

    fun createNewAudioFile(): File? {
        return taskLiveData.value?.let { task ->
            val newAudioDescriptionUI = AudioDescriptionUI(taskInternalId = task.internalId)
            newAudioDescriptionUI.getAudioFile(filesDir)
        }
    }

    fun initobserve() {
        taskLiveData.observeForever(taskLiveDataObserver)
    }

    fun addNewPhotoDescription(photoDescriptionUI: PhotoDescriptionUI){
        viewModelScope.launch {
            descriptionsRepository.addPhotoDescription(photoDescriptionUI)

        }
    }

    override fun onCleared() {
        super.onCleared()
        taskLiveData.removeObserver(taskLiveDataObserver)
    }
}