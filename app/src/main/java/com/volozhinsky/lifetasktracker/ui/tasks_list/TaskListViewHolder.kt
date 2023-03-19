package com.volozhinsky.lifetasktracker.ui.tasks_list

import androidx.recyclerview.widget.RecyclerView
import com.volozhinsky.lifetasktracker.databinding.RvItemTaskBinding
import com.volozhinsky.lifetasktracker.ui.models.TaskUI

class TaskListViewHolder(private val itemBinding: RvItemTaskBinding, private val onClicFunc: (String) -> Unit) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: TaskUI){
        itemBinding.tvTaskTitle.text = item.title
        itemBinding.tvTaskDate.text = item.due.toString()
        itemBinding.root.setOnClickListener{
            onClicFunc.invoke(item.id)
        }
    }
}