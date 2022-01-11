package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DatabaseAnime::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animeId")
        ),
        ForeignKey(
            entity = DatabaseAnimeStatus::class,
            parentColumns = arrayOf("animeId"),
            childColumns = arrayOf("animeStatusId")
        )
    ]
)
data class DatabaseAnimeList(
    @PrimaryKey
    val id: Int,
    val animeId: Int,
    val animeStatusId: Int,
)
