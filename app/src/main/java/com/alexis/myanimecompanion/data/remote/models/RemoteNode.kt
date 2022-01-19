package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.domain.Anime
import com.squareup.moshi.Json

data class Node(
    val id: Int,
    val title: String,
    @Json(name = "main_picture")
    val mainPicture: RemoteMainPicture
)

fun Node.asAnime(): Anime {
    return Anime(id, title, mainPicture.large)
}
