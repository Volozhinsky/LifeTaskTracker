package com.volozhinsky.lifetasktracker.ui.task_details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.FragmentTaskDetailTopBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailTopFragment : Fragment() {

    private var _binding: FragmentTaskDetailTopBinding? = null
    private val binding get() = _binding!!
    private val args: TaskDetailTopFragmentArgs by navArgs()
    private val viewModel by viewModels<TaskDetailTopViewModel>()
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
        initLiveData()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveTask()
    }

    private fun initLiveData() {
        viewModel.getTask(args.taskInternalId)
     }

    private fun initViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        initTextTitle()
    }

    private fun initTextTitle(){
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.taskLiveData.value?.let {
                    it.title = p0.toString()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.etTitle.addTextChangedListener(titleWatcher)
    }
}