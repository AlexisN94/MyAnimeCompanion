package com.alexis.myanimecompanion.data

import android.content.Context
import android.util.Base64
import android.util.Log
import com.alexis.myanimecompanion.TokenStorageManager
import com.alexis.myanimecompanion.data.remote.APIClient
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import com.alexis.myanimecompanion.data.remote.models.Details
import com.alexis.myanimecompanion.data.remote.models.User
import com.alexis.myanimecompanion.data.remote.models.asDomainModel
import com.alexis.myanimecompanion.data.remote.models.asListOfAnime
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainToken
import retrofit2.HttpException
import java.security.SecureRandom

private const val TAG = "RemoteDataSource"

class RemoteDataSource private constructor() {
    private var myAnimeListApi: MyAnimeListAPI = APIClient.myAnimeListApi
    private lateinit var codeVerifier: String
    private lateinit var codeChallenge: String
    private lateinit var tokenStorageManager: TokenStorageManager

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

    suspend fun getAnimeDetails(anime: Anime): Details? {
        return getAnimeDetails(anime.id)
    }

    /**
     * Since anime details aren't stored in the database, we use them directly by returning Anime
     */
    suspend fun getAnimeDetails(animeId: Int): Details? {
        val token = getNonExpiredToken()

        return try {
            myAnimeListApi.getAnimeDetails(
                token?.accessToken,
                animeId
            )
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun updateAnimeStatus(anime: Anime): Boolean {
        val token = getNonExpiredToken()

        if (token?.accessToken != null && anime.myListStatus != null) {
            try {
                myAnimeListApi.updateAnimeStatus(
                    token.accessToken,
                    anime.myListStatus.animeId,
                    anime.myListStatus.status,
                    anime.myListStatus.episodesWatched,
                    anime.myListStatus.score
                )
            } catch (e: HttpException) {
                false
            }
        }
        return true
    }

    private suspend fun getAccessToken(authorizationCode: String) {
        val params = mutableMapOf<String, String>()

        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("code", authorizationCode)
            put("code_verifier", codeVerifier)
            put("grant_type", "authorization_code")
        }

        try {
            val token = myAnimeListApi.getAccessToken(params).asDomainModel()
            token?.let { tokenStorageManager.setToken(it) }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
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

    private suspend fun getNonExpiredToken(): DomainToken? {
        if (tokenStorageManager.checkExpired()) {
            refreshAccessToken()
        }
        return tokenStorageManager.getToken()
    }

    private suspend fun refreshAccessToken() {
        var token = tokenStorageManager.getToken()

        token?.let {
            val params = mutableMapOf<String, String>()
            params.apply {
                put("client_id", APIClient.MAL_CLIENT_ID)
                put("grant_type", "refresh_token")
                put("refresh_token", it.refreshToken)
            }
            try {
                token = myAnimeListApi.refreshAccessToken(params).asDomainModel()
                token?.let { tokenStorageManager.setToken(it) }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    suspend fun getUser(): User? {
        val token = getNonExpiredToken()

        return try {
            myAnimeListApi.getUserProfile("Bearer ${token?.accessToken}")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun onAuthorizationCodeReceived(authorizationCode: String) {
        getAccessToken(authorizationCode)
    }

    fun clearUser() {
        tokenStorageManager.clearToken()
    }

    fun getAnimeList(): List<Details>? {
        TODO("Not yet implemented")
    }

    suspend fun hasValidToken(): Boolean {
        return getNonExpiredToken() != null
    }

    companion object {
        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(context: Context): RemoteDataSource {
            synchronized(this) {
                return INSTANCE ?: RemoteDataSource().also { instance ->
                    val tokenStorageManager = TokenStorageManager.getInstance(context)
                    instance.tokenStorageManager = tokenStorageManager
                    INSTANCE = instance
                }
            }
        }
    }
}