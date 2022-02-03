package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeDetails
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.AnimeDetails
import com.alexis.myanimecompanion.domain.AnimeStatus
import com.alexis.myanimecompanion.toMALDate
import com.squareup.moshi.Json
import java.util.*

data class Details(
    @Json(name = "alternative_titles")
    val alternativeTitles: AlternativeTitles = AlternativeTitles(),
    val background: String = "",
    @Json(name = "end_date")
    val endDate: String = "",
    val genres: List<Genre> = listOf(),
    val id: Int = 0,
    @Json(name = "main_picture")
    val mainPicture: MainPicture = MainPicture(),
    val mean: Double = 0.0,
    val media_type: String = "",
    @Json(name = "my_list_status")
    val myListStatus: MyListStatus? = null,
    @Json(name = "num_episodes")
    val numEpisodes: Int = 0,
    val pictures: List<Picture> = listOf(),
    val popularity: Int = 0,
    val rank: Int = 0,
    val rating: String = "",
    val source: String = "",
    @Json(name = "start_date")
    val startDate: String = "",
    @Json(name = "start_season")
    val startSeason: StartSeason = StartSeason(),
    val status: String = "",
    val synopsis: String = "",
    val title: String = ""
)

/**
 * @return null if [myListStatus.updatedAt] fails to parse to [Date]
 */
fun Details.asDomainModel(): Anime? {
    val genreList: String = this.genres.joinToString(", ") { it.name }
    val parsedStartDate: Date? = startDate.toMALDate()
    val alternativeTitlesStr =
        "${alternativeTitles.en}" + ", " + "${alternativeTitles.ja}" + ", " + alternativeTitles.synonyms.joinToString(", ")

    val userStatus = myListStatus?.let {
        val parsedMyUpdatedAt = it.updatedAt.toMALDate() ?: return null
        AnimeStatus(it.score, it.status, it.numEpisodesWatched ?: it.numWatchedEpisodes ?: 0, parsedMyUpdatedAt)
    }

    val details = AnimeDetails(synopsis, genreList, parsedStartDate, mean, numEpisodes, status, alternativeTitlesStr)

    return Anime(id, title, mainPicture.large, userStatus, details)
}

/**
 * @return null if [myListStatus][Details.myListStatus] is null.
 * This may happen if an user authorization problem occurs.
 */
fun Details.asDatabaseModel(listStatus: MyListStatus? = null): DatabaseCompleteAnime? {
    val nonNullListStatus = myListStatus ?: listStatus ?: return null

    val genreList: String = this.genres.joinToString(", ") { it.name }
    val alternativeTitlesStr =
        "${alternativeTitles.en}" + ", " + "${alternativeTitles.ja}" + ", " + alternativeTitles.synonyms.joinToString(", ")

    return DatabaseCompleteAnime(
        DatabaseAnime(id, title, mainPicture.large),
        nonNullListStatus.asDatabaseModel(id),
        DatabaseAnimeDetails(
            id,
            synopsis,
            genreList,
            startDate,
            mean,
            numEpisodes,
            status,
            alternativeTitlesStr
        )
    )
}

/**
 * @return null when [myListStatus][Details.myListStatus] is null for any list item.
 * This may happen if an user authorization problem occurs.
 */
fun List<Details>.asDatabaseModel(): List<DatabaseCompleteAnime>? {
    val databaseAnimeList = mutableListOf<DatabaseCompleteAnime>()
    for (item in this) {
        val databaseAnimeWithStatus = item.asDatabaseModel() ?: return null
        databaseAnimeList.add(databaseAnimeWithStatus)
    }
    return databaseAnimeList
}
