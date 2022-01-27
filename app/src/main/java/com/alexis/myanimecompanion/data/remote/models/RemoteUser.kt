package com.alexis.myanimecompanion.data.remote.models

data class RemoteUser(
    val anime_statistics: RemoteAnimeStatistics,
    val id: Int,
    val joined_at: String,
    val location: String,
    val name: String
)
