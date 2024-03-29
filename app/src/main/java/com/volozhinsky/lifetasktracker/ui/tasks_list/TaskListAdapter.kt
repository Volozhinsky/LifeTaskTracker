package com.volozhinsky.lifetasktracker.ui.tasks_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.volozhinsky.lifetasktracker.databinding.RvItemTaskBinding
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import java.time.format.DateTimeFormatter

class TaskListAdapter(private val listners: TaskListVHListner,
                      private val formatter: DateTimeFormatter) :
    RecyclerView.Adapter<TaskListViewHolder>() {

    private val taskList: MutableList<TaskUI> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val taskBinding = RvItemTaskBinding.inflate(layoutInflater,parent,false)
        return TaskListViewHolder(taskBinding,listners,formatter)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int  = taskList.size


    fun setAdapterData(list: List<TaskUI>){
        taskList.clear()
        taskList.addAll(list)
        notifyDataSetChanged()
    }
}