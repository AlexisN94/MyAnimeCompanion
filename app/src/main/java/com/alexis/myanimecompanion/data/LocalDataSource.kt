package com.alexis.myanimecompanion.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseUser

private const val TAG = "LocalDataSource"

class LocalDataSource private constructor(private val database: AnimeDatabase) {

    fun insertOrUpdateUser(user: DatabaseUser) {
        if (database.userDao.update(user) == 0) {
            database.userDao.insert(user)
        }
    }

    fun getUser(): DatabaseUser? {
        return database.userDao.getUser()
    }

    fun insertOrUpdateAnime(completeAnime: DatabaseCompleteAnime) {
        val anime = completeAnime.anime
        val status = completeAnime.animeStatus
        val details = completeAnime.animeDetails

        if (database.animeDao.update(anime) == 0) {
            database.animeDao.insert(anime)
        }
        if (database.animeStatusDao.update(status) == 0) {
            database.animeStatusDao.insert(status)
        }
        if (database.animeDetailsDao.update(details) == 0) {
            database.animeDetailsDao.insert(details)
        }
    }

    fun getAnime(animeId: Int): DatabaseCompleteAnime? {
        return database.animeDao.getById(animeId)
    }

    fun deleteAnime(animeId: Int) {
        database.animeDao.deleteById(animeId)
        database.animeDetailsDao.deleteByAnimeId(animeId)
        database.animeStatusDao.deleteByAnimeId(animeId)
    }

    fun getAnimeList(): LiveData<List<DatabaseCompleteAnime>> {
        return database.animeDao.getAll()
    }

    fun insertOrUpdateAnimeList(animeList: List<DatabaseCompleteAnime>) {
        val animeArray = animeList.map { it.anime }.toTypedArray()
        val statusArray = animeList.map { it.animeStatus }.toTypedArray()
        val detailsArray = animeList.map { it.animeDetails }.toTypedArray()

        database.animeDao.insertAll(*animeArray)
        database.animeStatusDao.insertAll(*statusArray)
        database.animeDetailsDao.insertAll(*detailsArray)
        /*animeDatabase.animeDao.insert(animeList[0].anime)
        animeDatabase.animeDetailsDao.insert(animeList[0].animeDetails)
        animeDatabase.animeStatusDao.insert(animeList[0].animeStatus)*/
        Log.d(TAG, "insertOrUpdateAnimeList() called with: animeList")
    }

    /**
     * Only call on logout
     */
    fun clearAllTables() {
        return database.clearAllTables()
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
