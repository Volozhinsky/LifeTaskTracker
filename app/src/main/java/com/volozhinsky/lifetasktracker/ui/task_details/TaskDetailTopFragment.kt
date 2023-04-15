package com.volozhinsky.lifetasktracker.ui.task_details

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.FragmentTaskDetailTopBinding
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.formatEmptyString
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class TaskDetailTopFragment : Fragment(), AudioDescriptionListVHListner {

    private var _binding: FragmentTaskDetailTopBinding? = null
    private val binding get() = _binding!!
    private val args: TaskDetailTopFragmentArgs by navArgs()
    private val viewModel by viewModels<TaskDetailTopViewModel>()
    private var photoRecyclerAdapter: PhotoDescriptionAdapter? = null
    private var audioRecyclerAdapter: AudioDescriptionAdapter? = null
    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { okFhoto ->
            if (okFhoto) {
                viewModel.newPhotoDescriptionUI?.let {
                    viewModel.addNewPhotoDescription(it)
                    viewModel.getTask(args.taskInternalId)
                }
            }
        }
    private val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    lateinit var grantedPermissions: Map<String, Boolean>
    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            grantedPermissions = it
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_task_detail_top, container, false)
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
        initAudioRecycler()
        initAddPhotoFab()
        initAudios()
    }

    private fun initAddPhotoFab() {
        binding.fabAddPhoto.setOnClickListener {
            val uri = viewModel.createNewPhotoFile()?.let { file ->
                FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, file)
            }
            photoLauncher.launch(uri)
        }
    }

    private fun initPhotoRecycler() {
        photoRecyclerAdapter = PhotoDescriptionAdapter()
        binding.rvPhotos.apply {
            adapter = photoRecyclerAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        viewModel.photoDescriptionList.observe(viewLifecycleOwner) {
            photoRecyclerAdapter?.setAdapterData(it)
        }
    }

    private fun initAudioRecycler() {
        audioRecyclerAdapter = AudioDescriptionAdapter(this, viewModel.formatter)
        binding.rvAudios.apply {
            adapter = audioRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        viewModel.audioDescriptionList.observe(viewLifecycleOwner) {
            audioRecyclerAdapter?.setAdapterData(it)
        }
    }

    private fun initCheckBoxStatus() {
        binding.cbStatus.setOnClickListener { view ->
            viewModel.taskLiveData.value?.let { task ->
                task.status = binding.cbStatus.isChecked
            }
        }
    }

    private fun initTextTitle() {
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

    private fun initTextNotes() {
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

    private fun initDateDue() {
        viewModel.taskLiveData.observe(viewLifecycleOwner) {
            binding.etDue.text =
                it.due.formatEmptyString(viewModel.formatter, getString(R.string.addDue))
        }

        binding.etDue.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(), { view, year, monthOfYear, dayOfMonth ->
                    viewModel.taskLiveData.value?.let { task ->
                        task.due = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, 0, 0)
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

    private fun initTimeDue() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { view, hour, minutes ->
                viewModel.taskLiveData.value?.let { task ->
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

    fun initAudios() {
        binding.cbTakeAudio.isChecked = viewModel.recordingInProgress
        binding.cbTakeAudio.setOnClickListener {
            grantedPermissions = configurePermissions()
            if (grantedPermissions.getValue(Manifest.permission.RECORD_AUDIO) ) {
                if (!viewModel.recordingInProgress) {
                    startRecording()
                } else {
                    stopRecording()
                }
            }
        }
    }

    private fun stopRecording() {
        viewModel.addNewAudioDescription()
        viewModel.getTask(args.taskInternalId)
        viewModel.stopRecording()
    }

    private fun startRecording() {
        viewModel.startRecording()
    }

    private fun configurePermissions(): Map<String, Boolean> {
        val mapConstruct = mutableMapOf<String, Boolean>()
        permissions.forEach {
            mapConstruct.put(
                it,
                (ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_GRANTED)
            )
        }
        if (mapConstruct.containsValue(false)) {
            shouldShowRequestPermissionRationale(makeTextForRequestPermissionRationale(mapConstruct))
            permissionsLauncher.launch(permissions)
        }
        return mapConstruct
    }

    private fun makeTextForRequestPermissionRationale(mapConstruct: Map<String, Boolean>): String {
        var strConstruct: String = ""
        mapConstruct.filter { !it.value }.forEach { s, b ->
            strConstruct += requireContext().resources.getStringArray(R.array.permission_msg)[permissions.indexOf(
                s
            )]
        }
        return strConstruct
    }

    override fun onItemClick(audioDescriptionUI: AudioDescriptionUI) {
        if (viewModel.recordingInProgress) {
            stopRecording()
            binding.cbTakeAudio.isChecked = false
        }
        if (viewModel.plaingInProgress)
            viewModel.stopPlaing()
        else
            viewModel.startPlaying(audioDescriptionUI.getAudioFile(requireContext().filesDir).toString())
    }

    companion object {
        private const val FILE_PROVIDER_AUTHORITY = "com.volozhinsky.lifetasktracker.fileprovider"
    }
}