package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeWithStatus
import com.squareup.moshi.Json

data class UserAnimeList(
    @Json(name = "data")
    val dataList: List<Data> = listOf(),
    val paging: Paging = Paging()
)

fun UserAnimeList.asDatabaseModel(): List<DatabaseAnimeWithStatus> {
    val animeList = mutableListOf<DatabaseAnimeWithStatus>()

    for ((node, listStatus) in this.dataList) {
        val anime = node.asDatabaseModel()
        val status = listStatus.asDatabaseModel(anime.id)
        animeList.add(DatabaseAnimeWithStatus(anime, status))
    }

    return animeList
}
