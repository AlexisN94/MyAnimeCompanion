package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class AnimeStatus(
    val episodesWatched: Int = 0,
    val totalEpisodes: Int = 0,
    val userScore: Double = 0.0,
    val updatedAt: Date = Date(),
) : Parcelable