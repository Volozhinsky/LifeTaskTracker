package com.volozhinsky.lifetasktracker.ui.tasks_list

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.FragmentTasksListTopBinding
import com.volozhinsky.lifetasktracker.ui.CallBacks
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksListTopFragment() : Fragment(),TaskListVHListner {

    private var _callBacks: CallBacks? = null
    private val callBacks get() = _callBacks
    private var _binding: FragmentTasksListTopBinding? = null
    private val binding get() = _binding!!
    private val tasksListTopViewModel by viewModels<TasksListTopViewModel>()
    private var recyclerAdapter: TaskListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_tasks_list_top,container,false)
        _binding = DataBindingUtil.bind(viewRoot)
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initCredential()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _callBacks = context as CallBacks?
    }

    override fun onDetach() {
        super.onDetach()
        _callBacks = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initCredential() {
        val louncher = registerForActivityResult(tasksListTopViewModel.chooseAccountContract){ activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val accountName = activityResult.data?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)
                accountName?.let {
                    tasksListTopViewModel.saveUserAccountName(it)
                    tasksListTopViewModel.updateData()
                }
            }
        }
        if (tasksListTopViewModel.getUserAccountName().isEmpty()){
            louncher.launch("")
        } else{
            tasksListTopViewModel.updateData()
        }
        val userRecoverableAuthlauncher = registerForActivityResult(tasksListTopViewModel.userRecoverableAuthContract){}
        tasksListTopViewModel.loadExIntent.observe(viewLifecycleOwner){
            userRecoverableAuthlauncher.launch(it)
        }

    }

    private fun initViews() {
        initRecycler()
        initSpinner()
        binding.fabAddTask.setOnClickListener {
            callBacks?.onTaskSelected("")
        }
        binding.cbShowCompleted.isChecked = tasksListTopViewModel.showCompleeted
        binding.cbShowCompleted.setOnClickListener {
            tasksListTopViewModel.showCompleeted = binding.cbShowCompleted.isChecked
            tasksListTopViewModel.updateTasks()
        }
     }

    private fun initSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_task_lists,
            mutableListOf<String>()
        )
        binding.taskListsSpinner.adapter = adapter
        binding.taskListsSpinner.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tasksListTopViewModel.changeSelectedTaskList(p2)
                tasksListTopViewModel.updateTasks()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        tasksListTopViewModel.tasksList.observe(viewLifecycleOwner){taskLists ->
            adapter.clear()
            adapter.addAll(taskLists.map { it.title })
            adapter.notifyDataSetChanged()
            tasksListTopViewModel.updateSelectedList()
        }
        tasksListTopViewModel.selectedTaskListIndex.observe(viewLifecycleOwner){
            binding.taskListsSpinner.setSelection(it)
        }
    }

    private fun initRecycler() {
        recyclerAdapter = TaskListAdapter(this,tasksListTopViewModel.formatter)
        binding.taskRecycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@TasksListTopFragment.context,
                LinearLayoutManager.VERTICAL,
                false)
        }
        tasksListTopViewModel.tasks.observe(viewLifecycleOwner){tasks ->
            recyclerAdapter?.setAdapterData(tasks)
        }
    }

    override fun onItemClick(task: TaskUI) {
        callBacks?.onTaskSelected(task.internalId.toString())
    }

    override fun onStartTiming(task: TaskUI) {

    }

    override fun onStatusClick(task: TaskUI) {
        tasksListTopViewModel.saveTask(task)
    }

}