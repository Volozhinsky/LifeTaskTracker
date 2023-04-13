package com.volozhinsky.lifetasktracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.ui.tasks_list.TasksListTopFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CallBacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val doneNavigateUpTop = findNavController(R.id.hostTopApp).navigateUp()
                if (!doneNavigateUpTop ) {
                    finish()
                }
            }
        })
    }

    override fun onTaskSelected(taskInternalId: String) {
        val actionTop = TasksListTopFragmentDirections.actionTasksListTopFragmentToTaskDetailTopFragment(taskInternalId)
        val navTop = findNavController(R.id.hostTopApp)
        navTop.navigate(actionTop)
    }
}