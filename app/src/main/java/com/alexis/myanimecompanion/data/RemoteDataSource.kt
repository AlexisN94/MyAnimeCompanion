package com.alexis.myanimecompanion.data

import android.util.Base64
import com.alexis.myanimecompanion.QueryFieldsBuilder
import com.alexis.myanimecompanion.data.remote.APIClient
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import com.alexis.myanimecompanion.data.remote.models.Token
import com.alexis.myanimecompanion.data.remote.models.asAnime
import com.alexis.myanimecompanion.data.remote.models.asListOfAnime
import com.alexis.myanimecompanion.domain.Anime
import java.security.SecureRandom

class RemoteDataSource private constructor() {
    private var myAnimeListApi: MyAnimeListAPI = APIClient.myAnimeListApi
    private var codeVerifier: String? = null
    private var codeChallenge: String? = null
    private var token: Token? = null

    /**
     * Since search results aren't stored in the database, we use them directly by returning List<Anime>
     */
    suspend fun search(q: String, limit: Int = 24, offset: Int = 0, fields: String = ""): List<Anime>? {
        val searchResult: List<Anime>? =
            try {
                val searchResult = myAnimeListApi.search(q, limit, offset, fields)
                val listOfAnime = searchResult.asListOfAnime()
                listOfAnime.map {
                    getAnimeDetails(it)
                }
                return listOfAnime
            } catch (e: Exception) {
                null
            }

        return searchResult
    }

    suspend fun getAnimeDetails(anime: Anime?): Anime? {
        return getAnimeDetails(anime?.id)
    }

    /**
     * Since anime details aren't stored in the database, we use them directly by returning Anime
     */
    suspend fun getAnimeDetails(animeId: Int?): Anime? {
        val anime: Anime? =
            try {
                if (animeId != null) {
                    myAnimeListApi.getAnimeDetails(
                        animeId,
                        token = token?.access_token
                    ).asAnime()
                } else
                    null
            } catch (e: Exception) {
                null
            }

        return anime
    }

    suspend fun updateAnimeStatus(token: String, anime: Anime?) {
        anime?.let {
            myAnimeListApi.updateAnimeStatus(token, it.id, it.userStatus, it.episodesWatched, it.userScore)
        }
    }

    suspend fun getAccessToken(authorizationCode: String): Boolean {
        val params = mutableMapOf<String, String>()
        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("code", authorizationCode)
            put("code_verifier", codeVerifier!!)
            put("grant_type", "authorization_code")
        }
        val bn = myAnimeListApi
        val n = bn::getAccessToken
        // TODO properly store token
        token = myAnimeListApi.getAccessToken(params)
        val a = token
        return token != null
    }

    fun getAuthorizationURL(): String {
        newCodeVerifierAndChallenge()

        return MyAnimeListAPI.BASE_AUTHORIZATION_URL +
                "?response_type=code" +
                "&client_id=${APIClient.MAL_CLIENT_ID}" +
                "&code_challenge=$codeChallenge"
    }

    private fun newCodeVerifierAndChallenge() {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        codeVerifier = Base64.encodeToString(bytes, Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP)

        // Only the 'plain' method is supported by MyAnimeList, so code challenge = code verifier
        codeChallenge = codeVerifier
    }

    suspend fun refreshAccessToken(refreshToken: String): Token? {
        val params = mutableMapOf<String, String>()
        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("grant_type", "refresh_token")
            put("refresh_token", refreshToken)
        }
        return myAnimeListApi.refreshAccessToken(params)
    }

    companion object {
        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(): RemoteDataSource {
            synchronized(this) {
                return INSTANCE ?: RemoteDataSource().also { instance ->
                    INSTANCE = instance
                }
            }
        }
    }
}
