package com.volozhinsky.lifetasktracker.ui.task_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.FragmentTaskDetailTopBinding
import com.volozhinsky.lifetasktracker.databinding.FragmentTasksListTopBinding
import com.volozhinsky.lifetasktracker.ui.tasks_list.TasksListBottomFragmentDirections
import com.volozhinsky.lifetasktracker.ui.tasks_list.TasksListTopFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailTopFragment : Fragment() {

    private var _binding: FragmentTaskDetailTopBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_task_detail_top,container,false)
        _binding = DataBindingUtil.bind(viewRoot)
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.button2.setOnClickListener {
            val action2 = TaskDetailBottomInfoFragmentDirections.actionTaskDetailBottomInfoFragmentToTaskDetailBottomPicFragment()
            val nav = findNavController()
            nav.navigate(action2)
        }
    }
}