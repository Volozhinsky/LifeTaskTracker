package com.volozhinsky.lifetasktracker.ui.tasks_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.volozhinsky.lifetasktracker.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksListBottomFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks_list_bottom, container, false)
    }
}