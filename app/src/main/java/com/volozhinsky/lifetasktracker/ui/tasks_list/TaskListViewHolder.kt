package com.volozhinsky.lifetasktracker.ui.tasks_list

import androidx.recyclerview.widget.RecyclerView
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.RvItemTaskBinding
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import java.time.format.DateTimeFormatter

class TaskListViewHolder(
    private val itemBinding: RvItemTaskBinding,
    private val listners: TaskListVHListner,
    private val formatter: DateTimeFormatter
) :   RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: TaskUI) {
        itemBinding.tvTaskTitle.text = item.title
        itemBinding.tvTaskDate.text = item.due.format(formatter)
        itemBinding.root.setOnClickListener {
            listners.onItemClick(item)
        }
        itemBinding.tvTaskTitle.setOnClickListener {
            listners.onStartTiming(item)
        }
        itemBinding.cbCompleted.isChecked = item.status
        itemBinding.cbCompleted.setOnClickListener {
            item.status =  itemBinding.cbCompleted.isChecked
            listners.onStatusClick(item)
        }
    }
}