package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.domain.Anime

data class SearchResult(
    val data: List<Data>,
    val paging: Paging
)

fun SearchResult.asListOfAnime(): List<Anime> {
    val animeList = mutableListOf<Anime>()

    for (data in this.data) {
        animeList.add(data.node.asAnime())
    }
    return animeList
}