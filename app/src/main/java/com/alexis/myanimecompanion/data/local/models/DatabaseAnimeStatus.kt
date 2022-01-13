package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseAnimeStatus(
    @PrimaryKey
    val animeId: Int,
    val score: Int?,
    val status: String,
    val episodesWatched: Int,
    val updatedAt: String
)

