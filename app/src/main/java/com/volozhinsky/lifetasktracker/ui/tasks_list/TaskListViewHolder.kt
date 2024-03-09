package com.volozhinsky.lifetasktracker.ui.tasks_list

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.volozhinsky.lifetasktracker.R
import com.volozhinsky.lifetasktracker.databinding.RvItemTaskBinding
import com.volozhinsky.lifetasktracker.ui.models.TaskUI
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.formatEmptyString
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLogResults.caseResult
import java.time.format.DateTimeFormatter

class TaskListViewHolder(
    private val itemBinding: RvItemTaskBinding,
    private val listners: TaskListVHListner,
    private val formatter: DateTimeFormatter
) :   RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: TaskUI) {
        itemBinding.tvTaskTitle.text = item.title
        itemBinding.tvTaskDate.text = item.due.formatEmptyString(formatter,"")
        itemBinding.root.setOnClickListener {
            listners.onItemClick(item)
        }
        itemBinding.cbWorkOn.isChecked = item.activeTask
        itemBinding.cbWorkOn.setOnClickListener {

            listners.onStartTiming(item, (it as MaterialCheckBox).isChecked)
        }
        itemBinding.cbCompleted.isChecked = item.status
        itemBinding.cbCompleted.setOnClickListener {
            item.status =  itemBinding.cbCompleted.isChecked
            listners.onStatusClick(item)
        }
        itemBinding.tvLogDays.text = item.logDays.caseResult(itemBinding.root.context.resources.getStringArray(R.array.dayCases))
        itemBinding.tvLogHours.text = item.logHours.caseResult(itemBinding.root.context.resources.getStringArray(R.array.hourCases))
        itemBinding.tvLogMinutes.text = item.logMinutes.caseResult(itemBinding.root.context.resources.getStringArray(R.array.minutesCases))
    }
}