package com.volozhinsky.lifetasktracker.ui.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.io.File
import java.util.*

data class PhotoDescriptionUI(
    val id: UUID = UUID.randomUUID(),
    val taskInternalId: UUID,
){
    val photoFilename:String
        get() = "IMG_"+id.toString()+".jpg"
    fun getPhotoFile(filesDir: File): File {
        return File(filesDir,photoFilename)
    }
}
