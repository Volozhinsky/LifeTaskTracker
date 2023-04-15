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

    fun setShowCompleted(value: Boolean) = prefs.edit{
        putBoolean(SHOW_COMPLEETED,value)
    }

    fun getShowCompleted(): Boolean = prefs.getBoolean(SHOW_COMPLEETED,false)

    fun setCurrentTaskID(value: String) = prefs.edit{
        putString(CURRENT_TASK_ID,value)
    }

    fun getCurrentTaskId() = prefs.getString(CURRENT_TASK_ID, "").orEmpty()

    fun setCurrentTaskStartDate(value: Long) = prefs.edit{
        putLong(CURRENT_TASK_START_DATE,value)
    }

    fun getCurrentTaskStartDate() = prefs.getLong(CURRENT_TASK_START_DATE,0)

    companion object {
        private const val ACCOUNT_NAME_KEY = "ACCOUNT_NAME_KEY"
        private const val SELECTED_TASK_LIST_ID = "SELECTED_TASK_LIST_ID"
        private const val EMPTY_STRING = ""
        private const val SHOW_COMPLEETED = "SHOW_COMPLEETED"
        private const val CURRENT_TASK_ID = "CURRENT_TASK_ID"
        private const val CURRENT_TASK_START_DATE = "CURRENT_TASK_START_DATE"
    }
}