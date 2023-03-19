package com.volozhinsky.lifetasktracker.ui.tasks_list

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.UserRecoverableAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.domain.GetTasksListUseCase
import com.volozhinsky.lifetasktracker.domain.GetTasksUseCase
import com.volozhinsky.lifetasktracker.ui.ChooseAccountContract
import com.volozhinsky.lifetasktracker.ui.GoogleTasksRepository
import com.volozhinsky.lifetasktracker.ui.UserRecoverableAuthContract
import com.volozhinsky.lifetasktracker.ui.mappers.TaskListMapperUI
import com.volozhinsky.lifetasktracker.ui.mappers.TaskMapperUI
import com.volozhinsky.lifetasktracker.ui.models.TaskListUI
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@HiltViewModel
class TasksListTopViewModel @Inject constructor(
    private val prefs: UserDataSource,
    //  private val credential: GoogleAccountCredential, //Q нужно ли доп поле для доступа?
    val chooseAccountContract: ChooseAccountContract,
    val userRecoverableAuthContract: UserRecoverableAuthContract,
    private val repository: GoogleTasksRepository,
    private val getTasksListUseCase: GetTasksListUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val taskListMapperUI: TaskListMapperUI,
    private val taskMapperUI: TaskMapperUI
) : ViewModel() {

    private var _tasksList = MutableLiveData<List<TaskListUI>>()
    val tasksList get() = _tasksList
    private var _tasks = MutableLiveData<List<TaskUI>>()
    val tasks get() = _tasks
    private var _loadExIntent = MutableLiveData<Intent>()
    val loadExIntent get() = _loadExIntent
    private var _selectedTaskListIndex = MutableLiveData<Int>()
    val selectedTaskListIndex get() = _selectedTaskListIndex
    private val exceptionHandler = CoroutineExceptionHandler { _, ex ->
        when (ex) {
            is UserRecoverableAuthException -> {
                ex.intent?.let {
                    this._loadExIntent.value = it
                }
            }
            else -> throw ex
        }
    }

    fun saveUserAccountName(accountName: String) {
        prefs.setAccountName(accountName)
    }

    fun getUserAccountName() = prefs.getAccountName()

    fun updateData() {
        if (prefs.getAccountName().isNotEmpty()) {
            viewModelScope.launch { updateTasks() }
            viewModelScope.launch(exceptionHandler) {
                repository.synchronizeTaskLists()
                updateTasks()
            }
        }
    }

    fun updateSelectedList(){
        val selectedTaskListId = prefs.getSelectedTaskListID()
        _tasksList.value?.let { tasklists ->
            _selectedTaskListIndex.value =
                tasklists.indexOf(tasklists.find { it.id == selectedTaskListId })
        }
    }

    private suspend fun updateTasks() {
        _tasksList.value =
            getTasksListUseCase.getTaskLists().map { taskListMapperUI.mapDomainToUi(it) }
        _tasks.value =
            getTasksUseCase.getTasks().map { taskMapperUI.mapDomainToUi(it) }
    }

    fun changeSelectedTaskList(listPos: Int) {
        tasksList.value?.get(listPos)?.let { prefs.setSelectedTaskListID(it.id) }
    }
}