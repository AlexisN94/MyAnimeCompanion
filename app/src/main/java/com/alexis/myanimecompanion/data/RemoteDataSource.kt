package com.alexis.myanimecompanion.data

import android.content.Context
import android.util.Base64
import android.util.Log
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

class RemoteDataSource private constructor() {
    private var myAnimeListApi: MyAnimeListAPI = APIClient.myAnimeListApi
    private lateinit var codeVerifier: String
    private lateinit var codeChallenge: String
    private lateinit var tokenStorageManager: TokenStorageManager

    private suspend fun <T> tryRequest(request: suspend () -> T?): Result<T> {
        val requestResult = try {
            request()
        } catch (e: HttpException) {
            Log.e(TAG, e.printStackTrace().toString())
            return when (e.code()) {
                HttpURLConnection.HTTP_FORBIDDEN -> Result.failure(Error.Generic)
                HttpURLConnection.HTTP_UNAUTHORIZED -> Result.failure(Error.Authorization)
                HttpURLConnection.HTTP_BAD_REQUEST -> Result.failure(Error.BadRequest)
                else -> Result.failure(Error.Network)
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, e.printStackTrace().toString())
            return Result.failure(Error.Network)
        } catch (e: UnknownHostException) {
            Log.e(TAG, e.printStackTrace().toString())
            return Result.failure(Error.Network)
        }

        return Result.success(requestResult)
    }

    /**
     * Since search results aren't stored in the database, we use them directly by returning List<Anime>
     */
    suspend fun trySearch(q: String, limit: Int = 24, offset: Int = 0, fields: String = ""): Result<SearchResult> {
        return tryRequest {
            myAnimeListApi.search(q, limit, offset, fields)
        }
    }

    /**
     * Since anime details aren't stored in the database, we use them directly by returning Anime
     */
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
                token.accessToken,
                anime.id,
                anime.myListStatus.status,
                anime.myListStatus.episodesWatched,
                anime.myListStatus.score
            )
        }
    }

    private suspend fun getAccessToken(authorizationCode: String): Result<Unit> {
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

    suspend fun tryGetUser(): Result<User> {
        val token = getNonExpiredToken()

        return tryRequest {
            myAnimeListApi.getUserProfile("Bearer ${token?.accessToken}")
        }
    }

    suspend fun onAuthorizationCodeReceived(authorizationCode: String): Result<Unit> {
        return getAccessToken(authorizationCode)
    }

    fun clearUser() {
        tokenStorageManager.clearToken()
    }

    suspend fun tryGetAnimeList(): Result<UserAnimeList> {
        val token = getNonExpiredToken() ?: return Result.failure(Error.Authorization)

        return tryRequest {
            myAnimeListApi.getUserAnimeList("Bearer ${token.accessToken}")
        }
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
