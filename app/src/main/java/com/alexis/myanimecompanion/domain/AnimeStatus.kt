package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeStatus
import com.alexis.myanimecompanion.toMALDateString
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class AnimeStatus(
    var score: Int = 0,
    var status: String = "watching",
    var episodesWatched: Int = 0,
    var updatedAt: Date? = null
) : Parcelable

fun AnimeStatus.asDatabaseModel(animeId: Int): DatabaseAnimeStatus {
    return DatabaseAnimeStatus(
        animeId,
        score,
        status,
        episodesWatched,
        updatedAt?.toMALDateString()
    )
}
