package com.alexis.myanimecompanion.data.remote

import com.alexis.myanimecompanion.data.remote.models.RemoteAnime
import com.alexis.myanimecompanion.data.remote.models.SearchResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MyAnimeListAPI {
    @GET("/anime")
    suspend fun search(
        @Query("q")
        query: String,

        @Query("limit")
        limit: Int = 100,

        @Query("offset")
        offset: Int = 0,

        @Query("fields")
        fields: String = ""
    ): SearchResult

    @GET("/anime/{anime_id}")
    suspend fun getAnimeById(
        @Path("anime_id")
        id: Int,

        @Query("fields")
        fields: String = ""
    ): RemoteAnime

    @GET("/users/{user_name}/animelist")
    suspend fun getUserList(
        @Path("user_name")
        username: String
    ) /*TODO*/

    companion object {
        const val CLIENT_ID = "cefa7feeaea5653f9eca4cd9c860ecca"
        const val BASE_URL = "https://api.myanimelist.net/v2"
    }
}