package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class AnimeDetails(
    val synopsis: String,
    val genres: String,
    val releaseDate: Date?,
    val globalScore: Double,
    val numEpisodes: Int,
    val status: String,
    val alternativeTitles: String,
) : Parcelable
