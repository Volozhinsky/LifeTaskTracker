package com.volozhinsky.lifetasktracker.ui.tasks_list

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.UserRecoverableAuthException
import com.volozhinsky.lifetasktracker.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.SynchronizeTasksUseCase
import com.volozhinsky.lifetasktracker.domain.TimeLogUseCase
import com.volozhinsky.lifetasktracker.domain.maincontrol.LifeTaskAppControl
import com.volozhinsky.lifetasktracker.ui.ChooseAccountContract
import com.volozhinsky.lifetasktracker.ui.UserRecoverableAuthContract
import com.volozhinsky.lifetasktracker.ui.mappers.TaskListMapperUI
import com.volozhinsky.lifetasktracker.ui.mappers.TaskMapperUI
import com.volozhinsky.lifetasktracker.ui.models.TaskListUI
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.format.DateTimeFormatter
import javax.inject.Named

@HiltViewModel
class TasksListTopViewModel @Inject constructor(
    private val prefs: UserDataSource,
    val chooseAccountContract: ChooseAccountContract,
    val userRecoverableAuthContract: UserRecoverableAuthContract,
    private val taskListMapperUI: TaskListMapperUI,
    private val taskMapperUI: TaskMapperUI,
    @Named("ui") val formatter: DateTimeFormatter,
    private val lifeTaskAppControl: LifeTaskAppControl
) : ViewModel() {

    private var _taskListLiveData = MutableLiveData<List<TaskListUI>>()
    val tasksListLiveData get() = _taskListLiveData

    private var _tasksLiveData = MutableLiveData<List<TaskUI>>()
    val tasksLiveData: LiveData<List<TaskUI>> get() = _tasksLiveData

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
            is IOException ->{
                ex.suppressedExceptions.forEach {oneOfEx ->
                    if (oneOfEx is UserRecoverableAuthException){
                        oneOfEx.intent?.let {exceptionIntent ->
                            this._loadExIntent.value = exceptionIntent
                        }
                    }
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
                lifeTaskAppControl.synchronizeTaskLists()

                _loadingProgressBarLiveData.value = false
            }
            viewModelScope.launch {
                lifeTaskAppControl.getTaskListsFlow().collect { taskList ->
                    val tasklists = taskList.map {
                        taskListMapperUI.mapDomainToUi(it)
                    }
                    _taskListLiveData.value = tasklists
                    val selectedTaskListId = prefs.getSelectedTaskListID()
                    _selectedTaskListIndex.value = tasklists.indexOf(tasklists.find { it.id == selectedTaskListId })
                    updateTasks()
                }
            }
        }
    }


    fun updateTasks() {
        viewModelScope.launch {
            val tasksUseCase = withContext(Dispatchers.IO){
                lifeTaskAppControl.getTasksFlow()
            }
            tasksUseCase.collectLatest { list ->
                _tasksLiveData.value = list.map {
                    taskMapperUI.mapDomainToUi(it)
                }
            }
        }
    }

    fun changeSelectedTaskList(listPos: Int) {
        tasksListLiveData.value?.get(listPos)?.let {
            prefs.setSelectedTaskListID(it.id)
            updateTasks()
        }

    }

    fun saveTask(taskUI: TaskUI) {
        viewModelScope.launch {
            lifeTaskAppControl.saveTask(taskMapperUI.mapUiToDomain(taskUI))
        }
    }

    fun startLog(taskUI: TaskUI) {
        viewModelScope.launch {
            lifeTaskAppControl.startLog(taskMapperUI.mapUiToDomain(taskUI))
        }
    }

    fun stopLog(taskUI: TaskUI) {
        viewModelScope.launch {
            lifeTaskAppControl.stopLog(taskMapperUI.mapUiToDomain(taskUI))
        }
    }
}