package com.alexis.myanimecompanion.data

import android.content.Context
import android.util.Base64
import android.util.Log
import com.alexis.myanimecompanion.TokenStorageManager
import com.alexis.myanimecompanion.createEncryptedSharedPreferences
import com.alexis.myanimecompanion.data.local.models.DatabaseUser
import com.alexis.myanimecompanion.data.remote.APIClient
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import com.alexis.myanimecompanion.data.remote.models.asAnime
import com.alexis.myanimecompanion.data.remote.models.asDatabaseModel
import com.alexis.myanimecompanion.data.remote.models.asDomainModel
import com.alexis.myanimecompanion.data.remote.models.asListOfAnime
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainToken
import retrofit2.HttpException
import java.security.SecureRandom

private const val TAG = "RemoteDataSource"

class RemoteDataSource private constructor() {
    private var myAnimeListApi: MyAnimeListAPI = APIClient.myAnimeListApi
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tokenStorageManager: TokenStorageManager
    private var token: DomainToken? = null // null if not logged in

    suspend fun search(query: String, limit: Int = 24, offset: Int = 0, fields: String = ""): List<Anime>? {
        return try {
            val searchResult = myAnimeListApi.search(query, limit, offset, fields)
            val listOfAnime = searchResult.asListOfAnime()
            listOfAnime.map {
                getAnimeDetails(it)
            }
            listOfAnime
        } catch (e: HttpException) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun getAnimeDetails(anime: Anime): Anime? {
        return getAnimeDetails(anime.id)
    }

    suspend fun getAnimeDetails(animeId: Int): Anime? {
        return try {
            myAnimeListApi.getAnimeDetails(token?.accessToken, animeId).asAnime()
        } catch (e: HttpException) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun updateAnimeStatus(anime: Anime) {
        token?.accessToken?.let {
            myAnimeListApi.updateAnimeStatus(
                it,
                anime.id,
                anime.userStatus,
                anime.episodesWatched,
                anime.userScore
            )
        }
    }

    private suspend fun getAccessToken(authorizationCode: String) {
        val codeVerifier = sharedPreferences.getString("codeVerifier", null) ?: return
        val params = mutableMapOf<String, String>()

        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("code", authorizationCode)
            put("code_verifier", codeVerifier)
            put("grant_type", "authorization_code")
        }

        try {
            token = myAnimeListApi.getAccessToken(params).asDomainModel()
            token?.let { tokenStorageManager.updateToken(it) }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun getAuthorizationUrl(): String {
        val codeVerifier = generateCodeVerifier()
        val codeChallenge = generateCodeChallenge(codeVerifier)

        sharedPreferences.edit().putString("codeVerifier", codeVerifier)

        return MyAnimeListAPI.BASE_AUTHORIZATION_URL +
                "?response_type=code" +
                "&client_id=${APIClient.MAL_CLIENT_ID}" +
                "&code_challenge=$codeChallenge"
    }

    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP)
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        // Currently MyAnimeList only supports the 'plain' code challenge method, so the code challenge = code verifier
        return codeVerifier
    }

    private suspend fun refreshAccessToken() {
        token?.let {
            val params = mutableMapOf<String, String>()
            params.apply {
                put("client_id", APIClient.MAL_CLIENT_ID)
                put("grant_type", "refresh_token")
                put("refresh_token", it.refreshToken)
            }
            try {
                token = myAnimeListApi.refreshAccessToken(params).asDomainModel()
                token?.let { tokenStorageManager.updateToken(it) }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    suspend fun getUser(): DatabaseUser? {
        if (token == null) {
            return null
        }
        if (tokenStorageManager.checkExpired()) {
            refreshAccessToken()
        }

        return try {
            myAnimeListApi.getUserProfile("Bearer ${token?.accessToken}").asDatabaseModel()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            null
        }
    }

    suspend fun onAuthorizationCodeReceived(authorizationCode: String) {
        getAccessToken(authorizationCode)
    }

    fun forgetUser() {
        tokenStorageManager.clearToken()
    }

    companion object {
        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(context: Context): RemoteDataSource {
            synchronized(this) {
                return INSTANCE ?: RemoteDataSource().also { instance ->
                    val tokenStorageManager = TokenStorageManager.getInstance(context)
                    instance.tokenStorageManager = tokenStorageManager
                    instance.token = tokenStorageManager.fetchToken()
                    instance.sharedPreferences = createEncryptedSharedPreferences(context, "secret_shared_prefs")

                    INSTANCE = instance
                }
            }
        }
    }
}
