package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class AnimeStatus(
    var score: Int?,
    var status: String,
    var episodesWatched: Int = 0,
    val updatedAt: Date?
) : Parcelable
