package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DatabaseUser(
    @PrimaryKey
    val id: Int,
    val username: String,
    val lastUpdate: String,
    val isOnlineAccount: Boolean = false,
)
