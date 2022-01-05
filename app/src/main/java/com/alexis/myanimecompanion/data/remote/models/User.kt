package com.alexis.myanimecompanion.data.remote.models

data class User(
    val anime_statistics: AnimeStatistics = AnimeStatistics(),
    val id: Int = 0,
    val joined_at: String = "",
    val location: String = "",
    val name: String = ""
)