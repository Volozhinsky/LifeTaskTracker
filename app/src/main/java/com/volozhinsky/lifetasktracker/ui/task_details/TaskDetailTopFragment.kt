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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.FragmentTaskDetailTopBinding
import com.volozhinsky.lifetasktracker.ui.tasks_list.TaskListAdapter
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.formatEmptyString
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class TaskDetailTopFragment : Fragment() {

    private var _binding: FragmentTaskDetailTopBinding? = null
    private val binding get() = _binding!!
    private val args: TaskDetailTopFragmentArgs by navArgs()
    private val viewModel by viewModels<TaskDetailTopViewModel>()
    private var photoRecyclerAdapter:PhotoDescriptionAdapter? = null
    private val photoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {okFhoto ->
        if (okFhoto) {
            viewModel.newPhotoDescriptionUI?.let {
                viewModel.addNewPhotoDescription(it)
                viewModel.getTask(args.taskInternalId)
            }
        }
    }

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
        viewModel.initobserve()
        initTextTitle()
        initTextNotes()
        initDateDue()
        initCheckBoxStatus()
        initPhotoRecycler()
        initAddPhotoFab()
    }

    private fun initAddPhotoFab() {
        binding.fabAddPhoto.setOnClickListener {
            val uri =  viewModel.createNewPhotoFile()?.let {file ->
                FileProvider.getUriForFile(requireContext(),FILE_PROVIDER_AUTHORITY,file)
            }
            photoLauncher.launch(uri)
        }
    }

    private fun initPhotoRecycler() {
        photoRecyclerAdapter = PhotoDescriptionAdapter()
        binding.rvPhotos.apply {
            adapter = photoRecyclerAdapter
            layoutManager = GridLayoutManager(this.context,3)
        }

        viewModel.photoDescriptionList.observe(viewLifecycleOwner){
            photoRecyclerAdapter?.setAdapterData(it)
        }
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
        viewModel.taskLiveData.observe(viewLifecycleOwner){
            binding.etDue.text = it.due.formatEmptyString(viewModel.formatter,getString(R.string.addDue))
        }

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

    companion object{
        private const val FILE_PROVIDER_AUTHORITY = "com.volozhinsky.lifetasktracker.fileprovider"
    }
}