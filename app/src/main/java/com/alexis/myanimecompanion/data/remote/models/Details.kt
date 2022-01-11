package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.domain.Anime
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.*

data class Details(
    @Json(name = "alternative_titles")
    val alternativeTitles: AlternativeTitles,
    val background: String,
    @Json(name = "end_date")
    val endDate: String,
    val genres: List<Genre>,
    val id: Int,
    @Json(name = "main_picture")
    val mainPicture: MainPicture,
    val mean: Double,
    val media_type: String,
    @Json(name = "my_list_status")
    val myListStatus: MyListStatus?,
    val num_episodes: Int,
    val pictures: List<Picture>,
    val popularity: Int,
    val rank: Int,
    val rating: String,
    val source: String,
    @Json(name = "start_date")
    val startDate: String,
    @Json(name = "start_season")
    val startSeason: StartSeason,
    val status: String,
    val synopsis: String,
    val title: String
)

fun Details.asAnime(): Anime {
    val genreList: List<String> = this.genres.map { it.name }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX")
    val parsedStartDate: Date? = try {
        dateFormat.parse(startDate)
    } catch (e: Exception) {
        null
    }
    val parsedUpdatedAt: Date? = try {
        dateFormat.parse(myListStatus?.updatedAt)
    } catch (e: Exception) {
        null
    }
    val alternativeTitlesStr = "${alternativeTitles.en + ", "}" +
            "${alternativeTitles.ja + ", "}${alternativeTitles.synonyms.joinToString(", ")}"

    return Anime(
        id,
        title,
        mainPicture.medium,
        synopsis,
        genreList.joinToString(", "),
        parsedStartDate,
        mean,
        num_episodes,
        myListStatus?.numEpisodesWatched ?: 0,
        myListStatus?.score,
        myListStatus?.status,
        parsedUpdatedAt,
        status,
        alternativeTitlesStr
    )
}