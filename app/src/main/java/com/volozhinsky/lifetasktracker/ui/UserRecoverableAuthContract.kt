package com.volozhinsky.lifetasktracker.ui

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract

class UserRecoverableAuthContract (): ActivityResultContract<Intent, ActivityResult>() {
    override fun createIntent(context: Context, inputIntent: Intent): Intent {
        return inputIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
        return ActivityResult(resultCode, intent)
    }
}