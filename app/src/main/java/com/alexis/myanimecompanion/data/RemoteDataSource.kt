package com.alexis.myanimecompanion.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.alexis.myanimecompanion.data.remote.APIClient
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import com.alexis.myanimecompanion.data.remote.models.RemoteToken
import com.alexis.myanimecompanion.data.remote.models.asAnime
import com.alexis.myanimecompanion.data.remote.models.asListOfAnime
import com.alexis.myanimecompanion.domain.Anime
import retrofit2.HttpException
import java.security.SecureRandom

class RemoteDataSource private constructor() {
    private var myAnimeListApi: MyAnimeListAPI = APIClient.myAnimeListApi
    private lateinit var sharedPreferences: SharedPreferences
    private var token: RemoteToken? = null

    suspend fun search(query: String, limit: Int = 24, offset: Int = 0, fields: String = ""): List<Anime>? {
        val searchResult: List<Anime>? =
            try {
                val searchResult = myAnimeListApi.search(query, limit, offset, fields)
                val listOfAnime = searchResult.asListOfAnime()
                listOfAnime.map {
                    getAnimeDetails(it)
                }
                return listOfAnime
            } catch (e: HttpException) {
                null
            }

        return searchResult
    }

    suspend fun getAnimeDetails(anime: Anime): Anime? {
        return getAnimeDetails(anime.id)
    }

    suspend fun getAnimeDetails(animeId: Int): Anime? {
        val anime: Anime? =
            try {
                if (animeId != null) {
                    myAnimeListApi.getAnimeDetails(animeId, token = token?.access_token).asAnime()
                } else {
                    null
                }
            } catch (e: HttpException) {
                null
            }

        return anime
    }

    suspend fun updateAnimeStatus(token: String, anime: Anime) {
        myAnimeListApi.updateAnimeStatus(token, anime.id, anime.userStatus, anime.episodesWatched, anime.userScore)
    }

    suspend fun getAccessToken(authorizationCode: String): Boolean {
        val codeVerifier = sharedPreferences.getString("codeVerifier", null) ?: return false

        val params = mutableMapOf<String, String>()
        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("code", authorizationCode)
            put("code_verifier", codeVerifier)
            put("grant_type", "authorization_code")
        }
        // TODO properly store token
        token = myAnimeListApi.getAccessToken(params)
        return token != null
    }

    fun getAuthorizationURL(): String {
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

    suspend fun refreshAccessToken(refreshToken: String): RemoteToken? {
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

        fun getInstance(context: Context): RemoteDataSource {
            synchronized(this) {
                return INSTANCE ?: RemoteDataSource().also { instance ->
                    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

                    instance.sharedPreferences = EncryptedSharedPreferences.create(
                        "secret_shared_prefs",
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )

                    INSTANCE = instance
                }
            }
        }
    }
}
