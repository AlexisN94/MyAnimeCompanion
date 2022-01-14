package com.alexis.myanimecompanion.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexis.myanimecompanion.domain.AnimeStatus
import com.alexis.myanimecompanion.toMALDate

@Entity
data class DatabaseAnimeStatus(
    @PrimaryKey
    val animeId: Int,
    val score: Int?,
    val status: String,
    val episodesWatched: Int,
    val updatedAt: String
)

/**
 * @return null when fails to parse [updatedAt][DatabaseAnimeStatus.updatedAt] to [Date][java.util.Date]
 */
fun DatabaseAnimeStatus.asDomainModel(): AnimeStatus? {
    updatedAt.toMALDate()?.let { parsedUpdatedAt ->
        return AnimeStatus(
            score,
            status,
            episodesWatched,
            parsedUpdatedAt
        )
    } ?: return null
}
