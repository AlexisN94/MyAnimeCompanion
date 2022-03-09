package com.alexis.myanimecompanion.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.alexis.myanimecompanion.data.local.models.asDomainModel
import com.alexis.myanimecompanion.data.remote.models.asDatabaseModel
import com.alexis.myanimecompanion.data.remote.models.asDomainModel
import com.alexis.myanimecompanion.data.remote.models.asListOfAnime
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainUser
import com.alexis.myanimecompanion.domain.asDatabaseModel
import com.alexis.myanimecompanion.toMALDate

private const val TAG = "AnimeRepository"

class AnimeRepository private constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun search(q: String, limit: Int, offset: Int): Result<List<Anime>?> {
        val animeList = remoteDataSource.trySearch(q, limit, offset).let { result ->
            result.getOrNull()?.asListOfAnime() ?: return Result.failure(result.errorOrNull()!!)
        }

        return Result.success(animeList)
    }

    /**
     * Errors – [Network][Error.Network], [Authorization][Error.Authorization],
     * [Generic][Error.Generic],  [OutdatedLocalData][Error.OutdatedLocalData], [NullUserStatus][Error.NullUserStatus]
     */
    suspend fun updateAnimeStatus(anime: Anime): Result<Unit> {
        if (isLoggedIn()) {
            tryUpdateRemoteAnime(anime).let { result ->
                if (result.isFailure) {
                    return result
                }
            }
        }

        val databaseAnime = anime.asDatabaseModel() ?: return Result.failure(Error.NullUserStatus)
        localDataSource.insertOrUpdateAnime(databaseAnime)
        return Result.success()
    }

    suspend fun addAnime(anime: Anime): Result<Unit> {
        if (isLoggedIn()) {
            remoteDataSource.tryUpdateAnimeStatus(anime).let { result ->
                if (result.isFailure) {
                    return Result.failure(result.errorOrNull()!!)
                }
            }
        }

        val databaseAnime = anime.asDatabaseModel() ?: return Result.failure(Error.NullUserStatus)
        localDataSource.insertOrUpdateAnime(databaseAnime)
        return Result.success()
    }

    /**
     * Errors – [Network][Error.Network], [Authorization][Error.Authorization],
     * [Generic][Error.Generic],  [OutdatedLocalData][Error.OutdatedLocalData]
     */
    suspend fun tryUpdateRemoteAnime(anime: Anime): Result<Unit> {
        val remoteAnime = remoteDataSource.tryGetAnimeDetails(anime).let {
            it.getOrNull() ?: return Result.failure(it.errorOrNull()!!)
        }

        // The API doesn't complain if client requests field `my_list_status` without authorization
        if (remoteAnime.myListStatus == null) {
            return Result.failure(Error.Authorization)
        }

        val remoteUpdatedAt = remoteAnime.myListStatus.updatedAt.toMALDate()
        val localUpdatedAt = anime.myListStatus?.updatedAt

        if (remoteUpdatedAt == null || localUpdatedAt == null) {
            return Result.failure(Error.Generic)
        }

        if (remoteUpdatedAt.before(localUpdatedAt)) {
            remoteDataSource.tryUpdateAnimeStatus(anime).let { result ->
                if (result.isFailure) {
                    return Result.failure(result.errorOrNull()!!)
                }
            }
        } else if (remoteUpdatedAt.after(localUpdatedAt)) {
            trySaveRemoteListToDatabase()
            return Result.failure(Error.OutdatedLocalData)
        }

        return Result.success()
    }

    suspend fun getAnime(anime: Anime): Result<Anime> {
        val anime = remoteDataSource.tryGetAnimeDetails(anime).let { result ->
            result.getOrNull()?.asDomainModel() ?: return Result.failure(result.errorOrNull()!!)
        }

        if (!isLoggedIn()) {
            val localAnime = localDataSource.getAnime(anime.id)?.asDomainModel()
            anime.myListStatus = anime.myListStatus ?: localAnime?.myListStatus
        }

        return Result.success(anime)
    }

    fun getAnimeList(): LiveData<List<Anime>> {
        val animeListLiveData = localDataSource.getAnimeList()

        return Transformations.map(animeListLiveData) { animeList ->
            animeList.map { anime ->
                anime.asDomainModel()
            }
        }
    }

    /**
     * Errors – [Network][Error.Network], [Authorization][Error.Authorization]
     */
    suspend fun refreshAnimeList(): Result<Unit> {
        if (isLoggedIn()) {
            Log.d(TAG, "Test " + "refreshAnimeList() called")
            trySaveRemoteListToDatabase().let { result ->
                if (result.isFailure) {
                    return Result.failure(result.errorOrNull()!!)
                }
            }
        }

        return Result.success()
    }

    /**
     * Errors – [Authorization][Error.Authorization], [Network][Error.Network]
     */
    private suspend fun trySaveRemoteListToDatabase(): Result<Unit> {
        val databaseAnimeList = remoteDataSource.tryGetAnimeList().let { result ->
            result.getOrNull()?.asDatabaseModel() ?: return Result.failure(result.errorOrNull()!!)
        }

        localDataSource.insertOrUpdateAnimeList(databaseAnimeList)
        return Result.success()
    }

    /**
     * Errors - [DatabaseQuery][Error.DatabaseQuery], [Authorization][Error.Authorization],
     * [Network][Error.Network]
     */
    suspend fun getUser(): Result<DomainUser> {
        if (!isLoggedIn()) {
            return Result.failure(Error.Authorization)
        }

        val remoteUser = remoteDataSource.tryGetUser().let { result ->
            result.getOrNull() ?: return Result.failure(result.errorOrNull()!!)
        }

        localDataSource.insertOrUpdateUser(remoteUser.asDatabaseModel())

        val domainUser = localDataSource.getUser()?.asDomainModel() ?: return Result.failure(Error.DatabaseQuery)
        return Result.success(domainUser)
    }

    fun logout() {
        localDataSource.clearAllTables()
        remoteDataSource.clearUser()
    }

    suspend fun isLoggedIn(): Boolean {
        return remoteDataSource.hasValidToken()
    }

    fun getAuthorizationUrl(): String {
        return remoteDataSource.getAuthorizationURL()
    }

    suspend fun requestToken(authorizationCode: String): Result<Unit> {
        return remoteDataSource.requestToken(authorizationCode)
    }

    suspend fun deleteAnime(animeId: Int): Result<Unit> {
        if (isLoggedIn()) {
            remoteDataSource.tryDeleteAnime(animeId)?.let { result ->
                if (result.isFailure) {
                    return Result.failure(result.errorOrNull()!!)
                }
            }
        }

        localDataSource.deleteAnime(animeId)
        return Result.success()
    }

    suspend fun postLogin() {
        localDataSource.clearAllTables()
        refreshAnimeList()
    }

    companion object {
        private var INSTANCE: AnimeRepository? = null

        fun getInstance(localDataSource: LocalDataSource, remoteDataSource: RemoteDataSource): AnimeRepository {
            synchronized(this) {
                return INSTANCE
                    ?: AnimeRepository(localDataSource, remoteDataSource)
                        .also { animeRepo ->
                            INSTANCE = animeRepo
                        }
            }
        }
    }
}
