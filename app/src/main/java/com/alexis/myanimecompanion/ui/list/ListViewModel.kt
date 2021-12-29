package com.alexis.myanimecompanion.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexis.myanimecompanion.domain.Anime
import java.util.*

class ListViewModel : ViewModel() {
    val animeList = MutableLiveData<List<Anime>>()

    init {
        // for testing
        animeList.value = listOf(
            Anime(
                0,
                "Ousama Ranking",
                "",
                "https://cdn.myanimelist.net/images/anime/1347/117616l.jpg",
                "",
                Date(),
                8.4,
                14,
            ),
            Anime(
                1,
                "Kimi ni Todoke",
                "",
                "https://cdn.myanimelist.net/images/anime/1340/110088l.jpg",
                "",
                Date(),
                9.0,
                12
            ),
        )
    }
}
