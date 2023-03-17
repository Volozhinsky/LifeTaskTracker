package com.volozhinsky.lifetasktracker.data.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun getAccountName(): String = prefs.getString(ACCOUNT_NAME_KEY, EMPTY_STRING).orEmpty()

    fun setAccountName(userName: String) = prefs.edit {
        putString(ACCOUNT_NAME_KEY, userName)
    }

    fun getSelectedTaskListID():String = prefs.getString(SELECTED_TASK_LIST_ID,EMPTY_STRING).orEmpty()

    fun setSelectedTaskListID(taskListId: String) = prefs.edit {
        putString(SELECTED_TASK_LIST_ID,taskListId)
    }

    companion object {
        private const val ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY"
        private const val SELECTED_TASK_LIST_ID = "SELECTED_TASK_LIST_ID"
        private const val EMPTY_STRING = ""
    }
}