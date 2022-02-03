package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import com.squareup.moshi.Json

data class UserAnimeList(
    @Json(name = "data")
    val dataList: List<Data> = listOf(),
    val paging: Paging = Paging()
)

fun UserAnimeList.asDatabaseModel(): List<DatabaseCompleteAnime> {
    val animeList = mutableListOf<DatabaseCompleteAnime>()

    for ((node, listStatus) in this.dataList) {
        animeList.add(node.asDatabaseModel(listStatus)!!)
    }

    return animeList
}
