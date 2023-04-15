package com.volozhinsky.lifetasktracker.ui.task_details

import androidx.recyclerview.widget.RecyclerView
import com.volozhinsky.lifetasktracker.databinding.RvItemAudioDescriptionBinding
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.utils.UtilsLocalDateTime.formatEmptyString
import java.time.format.DateTimeFormatter

class AudioDescriptionViewHolder(
    private val itemBinding: RvItemAudioDescriptionBinding,
    private val listeners: AudioDescriptionListVHListner,
    private val formatter: DateTimeFormatter
) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: AudioDescriptionUI) {
        itemBinding.textView.text = item.recordDate.formatEmptyString(formatter,"")
        itemBinding.textView.setOnClickListener {
            listeners.onItemClick(item)
        }
    }
}