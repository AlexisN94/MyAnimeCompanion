package com.alexis.myanimecompanion.data.remote.models

data class User(
    val anime_statistics: AnimeStatistics,
    val id: Int,
    val joined_at: String,
    val location: String,
    val name: String
)