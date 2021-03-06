package com.alexis.myanimecompanion.data.remote

import com.alexis.myanimecompanion.QueryFieldsBuilder
import com.alexis.myanimecompanion.data.remote.models.*
import retrofit2.http.*

interface MyAnimeListAPI {
    @GET("anime")
    suspend fun search(
        @Query("q") query: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("fields") fields: String? = null,
    ): SearchResult

    @GET("anime/{anime_id}")
    suspend fun getAnimeDetails(
        @Header("Authentication") token: String? = null,
        @Path("anime_id") animeId: Int,
        @Query("fields") fields: String? = QueryFieldsBuilder.fieldsForAnimeDetails().done()
    ): Details

    @GET("users/{user_name}/animelist")
    suspend fun getUserAnimeList(
        @Path("user_name") username: String
    ): RemoteUserAnimeList

    @GET("users/@me")
    suspend fun getUserProfile(
        @Header("Authorization") accessToken: String
    ): RemoteUser

    @DELETE("anime/{anime_id}/my_list_status")
    suspend fun deleteAnime(
        @Header("Authorization") accessToken: String,
        @Path("anime_id") animeId: Int,
    )

    // TODO PATCH or PUT ? documentation ambiguous
    @PATCH("anime/{anime_id}/my_list_status")
    suspend fun updateAnimeStatus(
        @Header("Authorization") accessToken: String,
        @Path("anime_id") animeId: Int,
        @Field("status") status: String? = null,
        @Field("num_watched_episodes") numWatchedEpisodes: Int? = null,
        @Field("score") score: Int? = null
    ): RemoteMyListStatus

    @FormUrlEncoded
    @POST(BASE_TOKEN_URL)
    suspend fun getAccessToken(
        @FieldMap params: Map<String, String>
    ): RemoteToken

    @FormUrlEncoded
    @POST(BASE_TOKEN_URL)
    suspend fun refreshAccessToken(
        @FieldMap params: Map<String, String>
    ): RemoteToken

    companion object {
        const val BASE_AUTHORIZATION_URL = "https://myanimelist.net/v1/oauth2/authorize"
        const val BASE_TOKEN_URL = "https://myanimelist.net/v1/oauth2/token"
        const val BASE_URL = "https://api.myanimelist.net/v2/"
    }
}
