package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.PhotoDescriptionEntity
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import javax.inject.Inject

class PhotoDescriptionMapper @Inject constructor(){

    fun mapEntityToUI(entity: PhotoDescriptionEntity) = with(entity){
        PhotoDescriptionUI(
            id = id,
            taskInternalId = taskInternalId
        )
    }

    fun mapUIToEntity(ui: PhotoDescriptionUI) = with(ui){
        PhotoDescriptionEntity(
            id = id,
            taskInternalId = taskInternalId
        )
    }
}