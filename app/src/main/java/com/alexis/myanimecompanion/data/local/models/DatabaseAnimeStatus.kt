package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DatabaseAnimeStatus(
    @PrimaryKey
    val animeId: Int,
    val score: Int,
    val episodesWatched: Int,
    val updatedAt: Date
)
