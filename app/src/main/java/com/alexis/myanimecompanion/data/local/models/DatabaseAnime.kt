package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseAnime(
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String,
    val numEpisodes: Int
)
