package com.volozhinsky.lifetasktracker.ui.tasks_list

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.UserRecoverableAuthException
import com.volozhinsky.lifetasktracker.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.GetTasksListUseCase
import com.volozhinsky.lifetasktracker.domain.GetTasksUseCase
import com.volozhinsky.lifetasktracker.domain.StartTimeLogUseCase
import com.volozhinsky.lifetasktracker.ui.ChooseAccountContract
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import com.volozhinsky.lifetasktracker.ui.UserRecoverableAuthContract
import com.volozhinsky.lifetasktracker.ui.mappers.TaskListMapperUI
import com.volozhinsky.lifetasktracker.ui.mappers.TaskMapperUI
import com.volozhinsky.lifetasktracker.ui.models.TaskListUI
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Named

@HiltViewModel
class TasksListTopViewModel @Inject constructor(
    private val prefs: UserDataSource,
    val chooseAccountContract: ChooseAccountContract,
    val userRecoverableAuthContract: UserRecoverableAuthContract,
    private val repository: GoogleTasksRepository,
    private val getTasksListUseCase: GetTasksListUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val startTimeLogUseCase: StartTimeLogUseCase,
    private val taskListMapperUI: TaskListMapperUI,
    private val taskMapperUI: TaskMapperUI,
    @Named("ui") val formatter: DateTimeFormatter,
) : ViewModel() {

    private var _taskListLiveData = MutableLiveData<List<TaskListUI>>()
    val tasksListLiveData get() = _taskListLiveData
    val tasks: LiveData<List<TaskUI>> get() = updateTasks()

    private var _loadExIntent = MutableLiveData<Intent>()
    val loadExIntent get() = _loadExIntent
    private var _selectedTaskListIndex = MutableLiveData<Int>()
    private var _loadingProgressBarLiveData = MutableLiveData<Boolean>()
    val loadingProgressBarLiveData get() = _loadingProgressBarLiveData
    val selectedTaskListIndex get() = _selectedTaskListIndex
    private val _errorliveData = MutableLiveData<Int>()
    val errorliveData: LiveData<Int> get() = _errorliveData
    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        when (ex) {
            is UserRecoverableAuthException -> {
                ex.intent?.let {
                    this._loadExIntent.value = it
                }
            }
            else -> {
                _errorliveData.value = R.string.unknownEx
                _loadingProgressBarLiveData.postValue(false)
            }
        }
    }
    var showCompleeted: Boolean
        get() = prefs.getShowCompleted()
        set(value) = prefs.setShowCompleted(value)

    fun saveUserAccountName(accountName: String) {
        prefs.setAccountName(accountName)
    }

    fun getUserAccountName() = prefs.getAccountName()

    fun updateData() {
        if (prefs.getAccountName().isNotEmpty()) {
            viewModelScope.launch(exceptionHandler) {
                _loadingProgressBarLiveData.value = true
                repository.synchronizeTaskLists()

                _loadingProgressBarLiveData.value = false
            }
            viewModelScope.launch {
                getTasksListUseCase.getTaskLists().collect{ taskList ->
                    _taskListLiveData.value = taskList.map { taskListMapperUI.mapDomainToUi(it)
                    }
                }
            }
        }
    }

    fun updateSelectedList() {
        val selectedTaskListId = prefs.getSelectedTaskListID()
        tasksListLiveData.value?.let { tasklists ->
            _selectedTaskListIndex.value =
                tasklists.indexOf(tasklists.find { it.id == selectedTaskListId })
        }
    }

    fun updateTasks(): LiveData<List<TaskUI>> {
        val activeTaskId = prefs.getCurrentTaskId()
        val tasksLiveData = getTasksUseCase.getTasks(showCompleeted)
        return Transformations.map(tasksLiveData){ tasks ->
            tasks.map { taskMapperUI.mapDomainToUi(it, it.internalId.toString() == activeTaskId) }
        }
    }

    fun changeSelectedTaskList(listPos: Int) {
        tasksListLiveData.value?.get(listPos)?.let { prefs.setSelectedTaskListID(it.id) }
    }

    fun saveTask(taskUI: TaskUI) {
        viewModelScope.launch {
            repository.saveTask(taskMapperUI.mapUiToDomain(taskUI))
            updateTasks()
        }
    }

    fun startLog(taskUI: TaskUI) {
        viewModelScope.launch {
            startTimeLogUseCase.startLog(taskMapperUI.mapUiToDomain(taskUI))
            updateTasks()
        }
    }

    fun stopLog() {
        viewModelScope.launch {
            startTimeLogUseCase.stopLog()
            updateTasks()
        }

    }
}