package com.alexis.myanimecompanion.data

import android.util.Base64
import android.util.Log
import com.alexis.myanimecompanion.data.remote.APIClient
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import com.alexis.myanimecompanion.data.remote.models.Token
import com.alexis.myanimecompanion.data.remote.models.asAnime
import com.alexis.myanimecompanion.data.remote.models.asDomainModel
import com.alexis.myanimecompanion.data.remote.models.asListOfAnime
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainToken
import java.security.SecureRandom

private const val TAG = "RemoteDataSource"

class RemoteDataSource private constructor() {
    private var myAnimeListApi: MyAnimeListAPI = APIClient.myAnimeListApi
    private lateinit var codeVerifier: String
    private lateinit var codeChallenge: String
    private var token: Token? = null // null if not logged in

    /**
     * Since search results aren't stored in the database, we use them directly by returning List<Anime>
     */
    suspend fun search(q: String, limit: Int = 24, offset: Int = 0, fields: String = ""): List<Anime>? {
        return try {
            val searchResult = myAnimeListApi.search(q, limit, offset, fields)
            val listOfAnime = searchResult.asListOfAnime()
            listOfAnime.map {
                getAnimeDetails(it)
            }
            return listOfAnime
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun getAnimeDetails(anime: Anime): Anime? {
        return getAnimeDetails(anime.id)
    }

    /**
     * Since anime details aren't stored in the database, we use them directly by returning Anime
     */
    suspend fun getAnimeDetails(animeId: Int): Anime? {
        return try {
            myAnimeListApi.getAnimeDetails(
                token?.access_token,
                animeId
            ).asAnime()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun updateAnimeStatus(accessToken: String, anime: Anime) {
        myAnimeListApi.updateAnimeStatus(
            accessToken,
            anime.id,
            anime.userStatus,
            anime.episodesWatched,
            anime.userScore
        )
    }

    suspend fun getAccessToken(authorizationCode: String): DomainToken? {
        val params = mutableMapOf<String, String>()

        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("code", authorizationCode)
            put("code_verifier", codeVerifier)
            put("grant_type", "authorization_code")
        }

        return try {
            myAnimeListApi.getAccessToken(params).asDomainModel()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
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

    suspend fun refreshAccessToken(refreshToken: String): DomainToken? {
        val params = mutableMapOf<String, String>()
        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("grant_type", "refresh_token")
            put("refresh_token", refreshToken)
        }
        return try {
            myAnimeListApi.refreshAccessToken(params).asDomainModel()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
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
