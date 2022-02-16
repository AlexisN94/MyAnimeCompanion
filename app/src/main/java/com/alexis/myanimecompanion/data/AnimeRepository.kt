package com.alexis.myanimecompanion.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import com.alexis.myanimecompanion.data.local.models.asDomainModel
import com.alexis.myanimecompanion.data.remote.models.asDatabaseModel
import com.alexis.myanimecompanion.data.remote.models.asDomainModel
import com.alexis.myanimecompanion.data.remote.models.asListOfAnime
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainUser
import com.alexis.myanimecompanion.domain.asDatabaseModel
import com.alexis.myanimecompanion.toMALDate

private const val TAG = "AnimeRepository"

class AnimeRepository private constructor() {
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource

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
    suspend fun insertOrUpdateAnimeStatus(anime: Anime): Result<Unit> {
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

    /**
     * Errors – [Network][Error.Network], [Authorization][Error.Authorization],
     * [Generic][Error.Generic],  [OutdatedLocalData][Error.OutdatedLocalData]
     */
    private suspend fun tryUpdateRemoteAnime(anime: Anime): Result<Unit> {
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

        return Result.success(anime)
    }

    /**
     * if not logged in then get anime from database
     *                  if (it or it.myListStatus) is null then return
     *
     */
    /*
    suspend fun getMergedAnime(remote: Anime, local: Anime): Result<Anime> {
        if(isLoggedIn()){

        }
        val remoteUpdatedAt = remote.myListStatus!!.updatedAt
        val localUpdatedAt = local.myListStatus!!.updatedAt

        if(remoteUpdatedAt.after(localUpdatedAt)) {
            return Result.failure(Error.OutdatedLocalData)
        }

        val mergedAnime =
            if (remote.id == local.id && (remote.title != local.title || remote.imageUrl != local.imageUrl)) {
                remote
            } else {
                Anime(remote.id, remote.title, remote.imageUrl, )
            }

        return Result.success(mergedAnime)
    }
     */

    fun getAnimeList(): LiveData<List<Anime>> {
        val animeListLiveData = localDataSource.getAnimeList()

        animeListLiveData?.let { animeListLiveData ->
            return Transformations.map(animeListLiveData) { animeList ->
                animeList.map { anime ->
                    anime.asDomainModel()
                }
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
     * Errors – [Network][Error.Network], [Authorization][Error.Authorization]
     */
    private suspend fun tryGetRemoteAnimeList(): Result<List<DatabaseCompleteAnime>> {
        val animeList = remoteDataSource.tryGetAnimeList().let { result ->
            result.getOrNull() ?: return Result.failure(result.errorOrNull()!!)
        }

        return Result.success(animeList.asDatabaseModel())
    }

    /**
     * Errors – [Authorization][Error.Authorization], [Network][Error.Network]
     */
    private suspend fun trySaveRemoteListToDatabase(): Result<Unit> {
        val databaseAnimeList = tryGetRemoteAnimeList().let { result ->
            result.getOrNull() ?: return Result.failure(result.errorOrNull()!!)
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

    companion object {
        private var INSTANCE: AnimeRepository? = null

        fun getInstance(context: Context): AnimeRepository {
            synchronized(this) {
                return INSTANCE
                    ?: AnimeRepository()
                        .also { animeRepo ->
                            animeRepo.localDataSource = LocalDataSource.getInstance(context)
                            animeRepo.remoteDataSource = RemoteDataSource.getInstance(context)
                            INSTANCE = animeRepo
                        }
            }
        }
    }
}
