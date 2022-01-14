package com.alexis.myanimecompanion.data.local.models

import androidx.room.Embedded
import androidx.room.Relation
import com.alexis.myanimecompanion.domain.Anime

data class DatabaseAnimeWithStatus(
    @Embedded val anime: DatabaseAnime,

    @Relation(
        parentColumn = "id",
        entityColumn = "animeId",
        entity = DatabaseAnimeStatus::class
    )
    val animeStatus: DatabaseAnimeStatus
)

fun DatabaseAnimeWithStatus.asDomainModel(): Anime? {
    animeStatus.asDomainModel()?.let { animeStatusAsDomainModel ->
        return Anime(
            anime.id,
            anime.title,
            anime.imageUrl,
            animeStatusAsDomainModel,
            null
        )
    } ?: return null
}

fun List<DatabaseAnimeWithStatus>.asDomainModel(): List<Anime>? {
    val animeList = mutableListOf<Anime>()

    for (item in this) {
        item.asDomainModel()?.let {
            animeList.add(it)
        } ?: return null
    }

    return animeList
}
