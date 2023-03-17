package com.volozhinsky.lifetasktracker.ui.tasks_list

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.UserRecoverableAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.volozhinsky.lifetasktracker.data.pref.UserDataSource
import com.volozhinsky.lifetasktracker.ui.ChooseAccountContract
import com.volozhinsky.lifetasktracker.ui.UserRecoverableAuthContract
import kotlinx.coroutines.CoroutineExceptionHandler

@HiltViewModel
class TasksListTopViewModel @Inject constructor(
    private val prefs: UserDataSource,
  //  private val credential: GoogleAccountCredential, //Q нужно ли доп поле для доступа?
    val chooseAccountContract: ChooseAccountContract,
    val userRecoverableAuthContract: UserRecoverableAuthContract
) : ViewModel() {

    private var _loadExIntent = MutableLiveData<Intent>()
    val loadExIntent get() = _loadExIntent
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


    fun saveUserAccountName(accountName: String){
        prefs.setAccountName(accountName)
    }

    fun getUserAccountName() = prefs.getAccountName()
}