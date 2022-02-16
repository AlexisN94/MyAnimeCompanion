package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexis.myanimecompanion.domain.DomainUser

@Entity
data class DatabaseUser(
    @PrimaryKey
    val id: Int = -1,
    val username: String = ""
)

fun DatabaseUser.asDomainModel(): DomainUser {
    return DomainUser(id, username)
}
