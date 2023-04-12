package com.volozhinsky.lifetasktracker.ui.models

import java.io.File
import java.util.*

data class AudioDescriptionUI(
    val id: UUID = UUID.randomUUID(),
    val taskInternalId: UUID,
) {
    val audioFilename: String
        get() = "AUDIO_" + id.toString() + ".3gp"

    fun getAudioFile(filesDir: File): File {
        return File(filesDir, audioFilename)
    }
}