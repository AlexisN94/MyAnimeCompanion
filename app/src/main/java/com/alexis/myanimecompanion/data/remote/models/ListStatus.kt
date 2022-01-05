package com.alexis.myanimecompanion.data.remote.models

data class ListStatus(
    val is_rewatching: Boolean = false,
    val num_episodes_watched: Int = 0,
    val num_watched_episodes: Int = 0,
    val score: Int = 0,
    val status: String = "",
    val updated_at: String = ""
)