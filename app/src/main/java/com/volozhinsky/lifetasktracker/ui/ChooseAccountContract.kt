package com.volozhinsky.lifetasktracker.ui

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class ChooseAccountContract @Inject constructor(
    private val credential: GoogleAccountCredential
    ) : ActivityResultContract<String, ActivityResult>() {

    override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
        return ActivityResult(resultCode, intent)
    }

    override fun createIntent(context: Context, input: String): Intent {
        return credential.newChooseAccountIntent()
    }
}