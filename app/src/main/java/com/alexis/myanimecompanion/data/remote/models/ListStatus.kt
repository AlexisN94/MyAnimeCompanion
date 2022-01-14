package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeStatus

data class ListStatus(
    val is_rewatching: Boolean = false,
    val num_episodes_watched: Int? = null, // keep!!
    val num_watched_episodes: Int? = null, // keep!!
    val score: Int = 0,
    val status: String = "",
    val updated_at: String = ""
)

fun ListStatus.asDatabaseModel(animeId: Int): DatabaseAnimeStatus {
    return DatabaseAnimeStatus(
        animeId,
        score,
        status,
        (num_watched_episodes ?: num_episodes_watched) ?: 0,
        updated_at
    )
}
