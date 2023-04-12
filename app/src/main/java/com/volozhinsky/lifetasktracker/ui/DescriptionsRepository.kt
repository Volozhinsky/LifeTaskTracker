package com.volozhinsky.lifetasktracker.ui

import com.volozhinsky.lifetasktracker.ui.models.AudioDescriptionUI
import com.volozhinsky.lifetasktracker.ui.models.PhotoDescriptionUI
import java.util.UUID

interface DescriptionsRepository {

    suspend fun getPhotoDescriptions(taskInternalId: UUID): List<PhotoDescriptionUI>

    suspend fun getAudioDescriptions(taskInternalId: UUID): List<AudioDescriptionUI>

    suspend fun addPhotoDescription(photoDescription: PhotoDescriptionUI)

    suspend fun addAudioDescription(audioDescriptionUI: AudioDescriptionUI)
}