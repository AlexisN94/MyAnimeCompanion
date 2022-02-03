package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.domain.Anime

data class SearchResult(
    val data: List<SearchResultData> = listOf(),
    // TODO use paging
    val paging: Paging = Paging()
)

fun SearchResult.asListOfAnime(): List<Anime> {
    val animeList = mutableListOf<Anime>()

    for (data in this.data) {
        animeList.add(data.node.asDomainModel())
    }
    return animeList
}
