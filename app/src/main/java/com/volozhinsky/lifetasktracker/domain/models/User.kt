package com.volozhinsky.lifetasktracker.domain.models

import java.util.UUID

data class User(
    val id: UUID,
    val accountName: String
)
