package com.volozhinsky.lifetasktracker.ui.task_details

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.maincontrol.LifeTaskAppControl
import com.volozhinsky.lifetasktracker.domain.models.Task
import com.volozhinsky.lifetasktracker.ui.mappers.TaskMapperUI
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TaskDetailTopViewModel @Inject constructor(
    private val taskMapperUI: TaskMapperUI,
    @Named("ui") val formatter: DateTimeFormatter,
    private val lifeTaskAppControl: LifeTaskAppControl,
    @Named("filesDir") private val filesDir: File,
    private val prefs: UserDataSource
) : ViewModel() {

    private var _taskLiveData = MutableLiveData<TaskUI>()
    val taskLiveData get() = _taskLiveData
    private var _photoDescriptionList = MutableLiveData<List<PhotoDescriptionUI>>()
    val photoDescriptionList get() = _photoDescriptionList
    var newPhotoDescriptionUI: PhotoDescriptionUI? = null
    var newAudioDescriptionUI: AudioDescriptionUI? = null
    private var _audioDescriptionList = MutableLiveData<List<AudioDescriptionUI>>()
    val audioDescriptionList get() = _audioDescriptionList
    var recordingInProgress = false
    var plaingInProgress = false
    val taskLiveDataObserver = Observer<TaskUI>() { task ->
        viewModelScope.launch {
            _photoDescriptionList.value =
                lifeTaskAppControl.getPhotoDescriptions(task.internalId)
        }
        viewModelScope.launch {
            _audioDescriptionList.value =
                lifeTaskAppControl.getAudioDescriptions(task.internalId)
        }
    }
    private var audioRecorder: MediaRecorder? = null
    private var audioPlayer: MediaPlayer? = null

    fun getTask(taskInternalId: String) {
        val activeTaskId = prefs.getCurrentTaskId()
        if (taskInternalId.isNotEmpty()) {
            viewModelScope.launch {
                val task = lifeTaskAppControl.getTask(taskInternalId)
                _taskLiveData.value =
                    taskMapperUI.mapDomainToUi(task)
            }
        } else {
            val task = Task()
            _taskLiveData.value =
                taskMapperUI.mapDomainToUi(task)
        }
    }

    fun saveTask() {
        taskLiveData.value?.let { taskUI ->
            viewModelScope.launch {
                lifeTaskAppControl.saveTask(taskMapperUI.mapUiToDomain(taskUI))
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
            newAudioDescriptionUI = AudioDescriptionUI(
                taskInternalId = task.internalId,
                recordDate = LocalDateTime.now()
            )
            newAudioDescriptionUI?.getAudioFile(filesDir)
        }
    }

    fun initobserve() {
        taskLiveData.observeForever(taskLiveDataObserver)
    }

    fun addNewPhotoDescription(photoDescriptionUI: PhotoDescriptionUI) {
        viewModelScope.launch {
            lifeTaskAppControl.addPhotoDescription(photoDescriptionUI)
        }
    }

    override fun onCleared() {
        super.onCleared()
        taskLiveData.removeObserver(taskLiveDataObserver)
    }

    fun startRecording() {
        taskLiveData.value?.let { task ->
            audioRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(createNewAudioFile())
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    prepare()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "prepare() failed")
                }
                start()
                recordingInProgress = true
            }
        }
    }

    fun stopRecording() {
        audioRecorder?.apply {
            stop()
            release()
        }
        audioRecorder = null
        recordingInProgress = false
    }

    fun startPlaying(fileName: String) {
        audioPlayer = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
                plaingInProgress = true
                setOnCompletionListener { stopPlaing() }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    fun stopPlaing() {
        audioPlayer?.release()
        audioPlayer = null
        plaingInProgress = false
    }

    fun addNewAudioDescription() {
        newAudioDescriptionUI?.let {
            viewModelScope.launch {
                lifeTaskAppControl.addAudioDescription(it)
            }
        }
    }

    companion object {
        const val LOG_TAG = "AudioRecord"
    }
}