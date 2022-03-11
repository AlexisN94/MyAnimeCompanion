package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeDetails
import com.alexis.myanimecompanion.toMALDateString
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class AnimeDetails(
    val synopsis: String,
    val genres: String,
    val releaseDate: Date?,
    val globalScore: Double,
    var numEpisodes: Int,
    var status: String,
    val alternativeTitles: String,
) : Parcelable

fun AnimeDetails.asDatabaseModel(animeId: Int): DatabaseAnimeDetails {
    return DatabaseAnimeDetails(
        animeId,
        synopsis,
        genres,
        releaseDate?.toMALDateString(),
        globalScore,
        numEpisodes,
        status,
        alternativeTitles
    )
}
