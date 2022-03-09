package com.alexis.myanimecompanion.data

import android.util.Base64
import android.util.Log
import com.alexis.myanimecompanion.QueryFieldsBuilder
import com.alexis.myanimecompanion.TokenStorageManager
import com.alexis.myanimecompanion.data.remote.APIClient
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import com.alexis.myanimecompanion.data.remote.models.*
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainToken
import retrofit2.HttpException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.SecureRandom

private const val TAG = "RemoteDataSource"

class RemoteDataSource private constructor(
    private val myAnimeListApi: MyAnimeListAPI,
    private val tokenStorageManager: TokenStorageManager
) {
    private lateinit var codeVerifier: String
    private lateinit var codeChallenge: String

    private suspend fun <T> tryRequest(request: suspend () -> T?): Result<T> {
        val requestResult = try {
            request()
        } catch (e: HttpException) {
            Log.e(TAG, "test " + e.printStackTrace().toString())
            return when (e.code()) {
                HttpURLConnection.HTTP_FORBIDDEN -> Result.failure(Error.Generic)
                HttpURLConnection.HTTP_UNAUTHORIZED -> Result.failure(Error.Authorization)
                HttpURLConnection.HTTP_BAD_REQUEST -> Result.failure(Error.BadRequest)
                else -> Result.failure(Error.Network)
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "test " + e.printStackTrace().toString())
            return Result.failure(Error.Network)
        } catch (e: UnknownHostException) {
            Log.e(TAG, "test " + e.printStackTrace().toString())
            return Result.failure(Error.Network)
        }

        return Result.success(requestResult)
    }

    suspend fun trySearch(q: String, limit: Int = 24, offset: Int = 0, fields: String = ""): Result<SearchResult> {
        return tryRequest {
            myAnimeListApi.search(q, limit, offset, fields)
        }
    }

    suspend fun tryGetAnimeDetails(anime: Anime): Result<Details> {
        val token = getNonExpiredToken()

        return tryRequest {
            myAnimeListApi.getAnimeDetails(
                token = token?.let { "Bearer ${token.accessToken}" },
                anime.id
            )
        }
    }

    suspend fun tryUpdateAnimeStatus(anime: Anime): Result<MyListStatus> {
        val token = getNonExpiredToken()

        if (token?.accessToken == null) {
            return Result.failure(Error.Authorization)
        }

        if (anime.myListStatus == null) {
            return Result.failure(Error.NullUserStatus)
        }

        return tryRequest {
            myAnimeListApi.updateAnimeStatus(
                "Bearer ${token.accessToken}",
                anime.id,
                anime.myListStatus!!.status,
                anime.myListStatus!!.episodesWatched,
                anime.myListStatus!!.score
            )
        }
    }

    suspend fun tryDeleteAnime(animeId: Int): Result<Unit> {
        val token = getNonExpiredToken()

        if (token?.accessToken == null) {
            return Result.failure(Error.Authorization)
        }

        return tryRequest {
            myAnimeListApi.deleteAnime(
                "Bearer ${token.accessToken}",
                animeId
            )
        }
    }

    suspend fun requestToken(authorizationCode: String): Result<Unit> {
        val params = mutableMapOf<String, String>()

        params.apply {
            put("client_id", APIClient.MAL_CLIENT_ID)
            put("code", authorizationCode)
            put("code_verifier", codeVerifier)
            put("grant_type", "authorization_code")
        }

        val requestResult = tryRequest { myAnimeListApi.getAccessToken(params) }

        if (requestResult.isFailure) {
            return Result.failure(requestResult.errorOrNull()!!)
        }

        val token = requestResult.getOrNull()!!
        tokenStorageManager.setToken(token.asDomainModel())

        return Result.success()
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

    suspend fun getNonExpiredToken(): DomainToken? {
        if (tokenStorageManager.checkExpired()) {
            refreshAccessToken()
        }
        return tokenStorageManager.getToken()
    }

    suspend fun refreshAccessToken() {
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

    suspend fun tryGetUser(): Result<User> {
        val token = getNonExpiredToken()

        return tryRequest {
            myAnimeListApi.getUserProfile("Bearer ${token?.accessToken}")
        }
    }

    fun clearUser() {
        tokenStorageManager.clearToken()
    }

    suspend fun tryGetAnimeList(): Result<UserAnimeList> {
        val token = getNonExpiredToken() ?: return Result.failure(Error.Authorization)

        return tryRequest {
            myAnimeListApi.getUserAnimeList(
                "Bearer ${token.accessToken}",
                QueryFieldsBuilder.fieldsForAnimeDetails().done()
            )
        }
    }

    suspend fun hasValidToken(): Boolean {
        return getNonExpiredToken() != null
    }

    companion object {
        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(api: MyAnimeListAPI, tokenStorageManager: TokenStorageManager): RemoteDataSource {
            synchronized(this) {
                return INSTANCE ?: RemoteDataSource(api, tokenStorageManager).also { instance ->
                    INSTANCE = instance
                }
            }
        }
    }
}
