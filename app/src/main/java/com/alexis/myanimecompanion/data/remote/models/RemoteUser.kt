package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseUser

data class RemoteUser(
    val anime_statistics: RemoteAnimeStatistics? = null,
    val id: Int,
    val joined_at: String,
    val location: String,
    val name: String
)

fun RemoteUser.asDatabaseModel(): DatabaseUser? {
    return DatabaseUser(id, name, lastUpdate = null, isOnlineAccount = true)
}
