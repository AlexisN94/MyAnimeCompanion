package com.alexis.myanimecompanion.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseUser

private const val TAG = "LocalDataSource"

class LocalDataSource private constructor(private val database: AnimeDatabase) {

    fun insertOrUpdateUser(user: DatabaseUser) {
        if (this.database.userDao.update(user) == 0) {
            this.database.userDao.insert(user)
        }
    }

    fun getUser(): DatabaseUser? {
        return this.database.userDao.getUser()
    }

    fun insertOrUpdateAnime(completeAnime: DatabaseCompleteAnime) {
        val anime = completeAnime.anime
        val status = completeAnime.animeStatus
        val details = completeAnime.animeDetails

        if (this.database.animeDao.update(anime) == 0) {
            this.database.animeDao.insert(anime)
        }
        if (this.database.animeStatusDao.update(status) == 0) {
            this.database.animeStatusDao.insert(status)
        }
        if (this.database.animeDetailsDao.update(details) == 0) {
            this.database.animeDetailsDao.insert(details)
        }
    }

    fun getAnime(animeId: Int): DatabaseCompleteAnime? {
        return this.database.animeDao.getById(animeId)
    }

    fun deleteAnime(animeId: Int) {
        this.database.animeDao.deleteById(animeId)
        this.database.animeDetailsDao.deleteByAnimeId(animeId)
        this.database.animeStatusDao.deleteByAnimeId(animeId)
    }

    fun getAnimeList(): LiveData<List<DatabaseCompleteAnime>> {
        return this.database.animeDao.getAll()
    }

    fun insertOrUpdateAnimeList(animeList: List<DatabaseCompleteAnime>) {
        val animeArray = animeList.map { it.anime }.toTypedArray()
        val statusArray = animeList.map { it.animeStatus }.toTypedArray()
        val detailsArray = animeList.map { it.animeDetails }.toTypedArray()

        this.database.animeDao.insertAll(*animeArray)
        this.database.animeStatusDao.insertAll(*statusArray)
        this.database.animeDetailsDao.insertAll(*detailsArray)
        /*animeDatabase.animeDao.insert(animeList[0].anime)
        animeDatabase.animeDetailsDao.insert(animeList[0].animeDetails)
        animeDatabase.animeStatusDao.insert(animeList[0].animeStatus)*/
        Log.d(TAG, "insertOrUpdateAnimeList() called with: animeList")
    }

    /**
     * Only call on logout
     */
    fun clearAllTables() {
        return this.database.clearAllTables()
    }

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(database: AnimeDatabase): LocalDataSource {
            synchronized(this) {
                return INSTANCE ?: LocalDataSource(database).also { instance ->
                    INSTANCE = instance
                }
            }
        }
    }
}
