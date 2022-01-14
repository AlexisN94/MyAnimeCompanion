package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.domain.Anime
import com.squareup.moshi.Json

data class Node(
    val id: Int = 0,
    val title: String = "",
    @Json(name = "main_picture")
    val mainPicture: MainPicture = MainPicture()
)

fun Node.asDomainModel(): Anime {
    return Anime(id, title, mainPicture.large)
}

fun Node.asDatabaseModel(): DatabaseAnime {
    return DatabaseAnime(id, title, mainPicture.large)
}