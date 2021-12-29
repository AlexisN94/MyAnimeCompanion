package com.alexis.myanimecompanion.data.remote.models

import com.squareup.moshi.Json

data class RemoteMyListStatus(
    val status: String = "",
    val score: Int = 0,
    @Json(name = "num_episodes_watched")
    val numEpisodesWatched: Int = 0,
    @Json(name = "is_rewatching")
    val isRewatching: Boolean = false,
    @Json(name = "updated_at")
    val updatedAt: String = ""
)
