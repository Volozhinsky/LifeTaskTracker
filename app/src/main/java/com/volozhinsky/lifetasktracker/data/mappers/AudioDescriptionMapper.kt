package com.volozhinsky.lifetasktracker.data.mappers

import com.volozhinsky.lifetasktracker.data.database.AudioDescriptionEntity
import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import java.time.LocalDateTime
import javax.inject.Inject

class AudioDescriptionMapper @Inject constructor(){

    fun mapEntityToUI(entity: AudioDescriptionEntity) = with(entity){
        AudioDescriptionUI(
            id = id,
            taskInternalId = taskInternalId,
            recordDate = recordDate
        )
    }

    fun mapUIToEntity(ui: AudioDescriptionUI, date: LocalDateTime) = with(ui){
        AudioDescriptionEntity(
            id = id,
            taskInternalId = taskInternalId,
            recordDate = date
        )
    }
}