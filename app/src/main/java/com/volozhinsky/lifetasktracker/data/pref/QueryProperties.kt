package com.volozhinsky.lifetasktracker.data.pref

import javax.inject.Inject

class QueryProperties @Inject constructor(
    private val userDataSource: UserDataSource
){
    val account get() = userDataSource.getAccountName()
    val taskListId get() = userDataSource.getSelectedTaskListID()

    val showComplete get() = userDataSource.getShowCompleted()
}