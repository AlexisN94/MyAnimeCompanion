package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexis.myanimecompanion.domain.DomainUser

@Entity
data class DatabaseUser(
    @PrimaryKey
    val id: Int = -1,
    val username: String = "",
    val lastUpdate: String?,
    val isOnlineAccount: Boolean = false,
)

fun DatabaseUser.asDomainUser(): DomainUser? {
    return DomainUser(id, username, lastUpdate, isOnlineAccount)
}
