package com.alexis.myanimecompanion.data.local.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class DatabaseAnimeWithStatus(
    @Embedded val anime: DatabaseAnime,

    @Relation(
        parentColumn = "id",
        entityColumn = "animeId"
    )
    val animeStatus: DatabaseAnimeStatus
)
