package com.volozhinsky.lifetasktracker.ui.task_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.volozhinsky.lifetasktracker.databinding.RvItemPhotoDescriptionBinding
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI

class PhotoDescriptionAdapter(): RecyclerView.Adapter<PhotoDescriptionViewHolder>() {

    private val photos:MutableList<PhotoDescriptionUI> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoDescriptionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val photoDescriptionBinding = RvItemPhotoDescriptionBinding.inflate(layoutInflater,parent,false)
        return PhotoDescriptionViewHolder(photoDescriptionBinding)
    }

    override fun onBindViewHolder(holder: PhotoDescriptionViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size
}