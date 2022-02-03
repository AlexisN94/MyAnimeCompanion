package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexis.myanimecompanion.domain.AnimeDetails
import com.alexis.myanimecompanion.toMALDate

@Entity
data class DatabaseAnimeDetails(
    @PrimaryKey
    val animeId: Int,
    val synopsis: String,
    val genres: String,
    val releaseDate: String? = null,
    val globalScore: Double,
    val numEpisodes: Int,
    val status: String,
    val alternativeTitles: String,
)


fun DatabaseAnimeDetails.asDomainModel(): AnimeDetails {
    return AnimeDetails(
        synopsis,
        genres,
        releaseDate?.toMALDate(),
        globalScore,
        numEpisodes,
        status,
        alternativeTitles
    )
}
