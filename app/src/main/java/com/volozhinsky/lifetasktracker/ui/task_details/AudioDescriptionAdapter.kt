package com.volozhinsky.lifetasktracker.ui.task_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.volozhinsky.lifetasktracker.databinding.RvItemAudioDescriptionBinding
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import java.time.format.DateTimeFormatter

class AudioDescriptionAdapter(
    private val listeners: AudioDescriptionListVHListner,
    private val formatter: DateTimeFormatter
) :
    RecyclerView.Adapter<AudioDescriptionViewHolder>() {

    private val audios: MutableList<AudioDescriptionUI> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioDescriptionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val audioDescriptionBinding =
            RvItemAudioDescriptionBinding.inflate(layoutInflater, parent, false)
        return AudioDescriptionViewHolder(audioDescriptionBinding, listeners,formatter)
    }

    override fun onBindViewHolder(holder: AudioDescriptionViewHolder, position: Int) {
        holder.bind(audios[position])
    }

    override fun getItemCount(): Int = audios.size

    fun setAdapterData(list: List<AudioDescriptionUI>) {
        audios.clear()
        audios.addAll(list)
        notifyDataSetChanged()
    }
}