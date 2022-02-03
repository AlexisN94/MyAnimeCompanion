package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeStatus
import com.squareup.moshi.Json

data class MyListStatus(
    val status: String = "",
    val score: Int = 0,
    @Json(name = "num_episodes_watched")
    val numEpisodesWatched: Int? = null, //keep null default!
    @Json(name = "num__watched_episodes")
    val numWatchedEpisodes: Int? = null, //keep null default!
    @Json(name = "is_rewatching")
    val isRewatching: Boolean = false,
    @Json(name = "updated_at")
    val updatedAt: String = ""
)

fun MyListStatus.asDatabaseModel(animeId: Int): DatabaseAnimeStatus {
    return DatabaseAnimeStatus(
        animeId,
        score,
        status,
        (numWatchedEpisodes ?: numEpisodesWatched) ?: 0,
        updatedAt
    )
}
