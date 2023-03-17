package com.volozhinsky.lifetasktracker.ui.task_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.FragmentTaskDetailBottomPicBinding
import com.volozhinsky.lifetasktracker.databinding.FragmentTaskDetailTopBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailBottomPicFragment : Fragment() {

    private var _binding: FragmentTaskDetailBottomPicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_task_detail_bottom_pic,container,false)
        _binding = DataBindingUtil.bind(viewRoot)
        return viewRoot
    }
}