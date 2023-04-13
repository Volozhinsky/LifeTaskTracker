package com.volozhinsky.lifetasktracker.ui.task_details

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.volozhinsky.lifetasktracker.databinding.RvItemPhotoDescriptionBinding
import com.volozhinsky.lifetasktracker.databinding.RvItemTaskBinding
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI

class PhotoDescriptionViewHolder(private val itemBinding: RvItemPhotoDescriptionBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(item: PhotoDescriptionUI) {
        val photofile = item.getPhotoFile(itemBinding.root.context.filesDir)
        Glide.with(itemBinding.imageView.context).load(photofile).into(itemBinding.imageView)
    }
}