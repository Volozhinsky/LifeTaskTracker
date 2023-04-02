package com.volozhinsky.lifetasktracker.ui

import java.util.UUID

interface CallBacks {

    fun onTaskSelected(taskInternalID: UUID)
}