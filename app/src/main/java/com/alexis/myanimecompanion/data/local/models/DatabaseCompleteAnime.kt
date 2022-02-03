package com.alexis.myanimecompanion.data.local.models

import androidx.room.Embedded
import androidx.room.Relation
import com.alexis.myanimecompanion.domain.Anime

data class DatabaseCompleteAnime(
    @Embedded val anime: DatabaseAnime,

    @Relation(
        parentColumn = "id",
        entityColumn = "animeId",
        entity = DatabaseAnimeStatus::class
    )
    val animeStatus: DatabaseAnimeStatus,

    @Relation(
        parentColumn = "id",
        entityColumn = "animeId",
        entity = DatabaseAnimeDetails::class
    )
    val animeDetails: DatabaseAnimeDetails
)

fun DatabaseCompleteAnime.asDomainModel(): Anime {
    return Anime(
        anime.id,
        anime.title,
        anime.imageUrl,
        animeStatus.asDomainModel(),
        animeDetails.asDomainModel()
    )
}

fun List<DatabaseCompleteAnime>.asDomainModel(): List<Anime> {
    return this.map { it.asDomainModel() }
}
