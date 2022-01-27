package com.alexis.myanimecompanion.data.remote.models

import com.squareup.moshi.Json

data class RemoteMyListStatus(
    val status: String,
    val score: Int,
    @Json(name = "num_episodes_watched")
    val numEpisodesWatched: Int,
    @Json(name = "is_rewatching")
    val isRewatching: Boolean,
    @Json(name = "updated_at")
    val updatedAt: String
)
