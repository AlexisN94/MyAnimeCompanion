package com.alexis.myanimecompanion.data.remote.models

data class RemoteListStatus(
    val is_rewatching: Boolean,
    val num_episodes_watched: Int,
    val num_watched_episodes: Int,
    val score: Int,
    val status: String,
    val updated_at: String
)