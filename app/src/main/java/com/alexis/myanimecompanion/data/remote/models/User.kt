package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseUser

data class User(
    val anime_statistics: AnimeStatistics = AnimeStatistics(),
    val id: Int = 0,
    val joined_at: String = "",
    val location: String = "",
    val name: String = ""
)

fun User.asDatabaseModel(): DatabaseUser? {
    return DatabaseUser(id, name, lastUpdate = null, isOnlineAccount = true)
}