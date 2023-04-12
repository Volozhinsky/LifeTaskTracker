package com.volozhinsky.lifetasktracker.ui.task_details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.time.LocalDateTime
import java.util.*

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
        initTextNotes()
        initDateDue()
        initCheckBoxStatus()
    }

    private fun initCheckBoxStatus() {
        binding.cbStatus.setOnClickListener { view ->
            viewModel.taskLiveData.value?.let{task ->
                task.status = binding.cbStatus.isChecked
            }
        }
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

    private fun initTextNotes(){
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.taskLiveData.value?.let {
                    it.notes = p0.toString()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.etNotes.addTextChangedListener(titleWatcher)
    }

    private fun initDateDue(){
        binding.etDue.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),{view, year, monthOfYear, dayOfMonth ->
                    viewModel.taskLiveData.value?.let {task ->
                        task.due = LocalDateTime.of(year,monthOfYear+1,dayOfMonth,0,0)
                    }
                    initTimeDue()
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    private fun initTimeDue(){
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { view, hour, minutes ->
                viewModel.taskLiveData.value?.let {task ->
                    task.due = task.due.plusHours(hour.toLong())
                    task.due = task.due.plusMinutes(minutes.toLong())
                    binding.etDue.text = task.due.format(viewModel.formatter)
                }
            },
            9,
            0,
            true
            )
        timePickerDialog.show()
    }
}