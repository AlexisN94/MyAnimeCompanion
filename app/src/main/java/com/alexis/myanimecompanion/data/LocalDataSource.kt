package com.alexis.myanimecompanion.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseUser

private const val TAG = "LocalDataSource"

class LocalDataSource private constructor() {
    private lateinit var animeDatabase: AnimeDatabase

    fun insertOrUpdateUser(user: DatabaseUser) {
        if (animeDatabase.userDao.update(user) == 0) {
            animeDatabase.userDao.insert(user)
        }
    }

    fun getUser(): DatabaseUser? {
        return animeDatabase.userDao.getUser()
    }

    fun insertOrUpdateAnime(completeAnime: DatabaseCompleteAnime) {
        val anime = completeAnime.anime
        val status = completeAnime.animeStatus
        val details = completeAnime.animeDetails

        if (animeDatabase.animeDao.update(anime) == 0) {
            animeDatabase.animeDao.insert(anime)
        }
        if (animeDatabase.animeStatusDao.update(status) == 0) {
            animeDatabase.animeStatusDao.insert(status)
        }
        if (animeDatabase.animeDetailsDao.update(details) == 0) {
            animeDatabase.animeDetailsDao.insert(details)
        }
    }

    fun getAnime(animeId: Int): DatabaseCompleteAnime? {
        return animeDatabase.animeDao.getById(animeId)
    }

    fun deleteAnime(animeId: Int) {
        animeDatabase.animeDao.deleteById(animeId)
        animeDatabase.animeDetailsDao.deleteByAnimeId(animeId)
        animeDatabase.animeStatusDao.deleteByAnimeId(animeId)
    }

    fun getAnimeList(): LiveData<List<DatabaseCompleteAnime>> {
        return animeDatabase.animeDao.getAll()
    }

    fun insertOrUpdateAnimeList(animeList: List<DatabaseCompleteAnime>) {
        val animeArray = animeList.map { it.anime }.toTypedArray()
        val statusArray = animeList.map { it.animeStatus }.toTypedArray()
        val detailsArray = animeList.map { it.animeDetails }.toTypedArray()

        animeDatabase.animeDao.insertAll(*animeArray)
        animeDatabase.animeStatusDao.insertAll(*statusArray)
        animeDatabase.animeDetailsDao.insertAll(*detailsArray)
        /*animeDatabase.animeDao.insert(animeList[0].anime)
        animeDatabase.animeDetailsDao.insert(animeList[0].animeDetails)
        animeDatabase.animeStatusDao.insert(animeList[0].animeStatus)*/
        Log.d(TAG, "insertOrUpdateAnimeList() called with: animeList")
    }

    /**
     * Only call on logout
     */
    fun clearAllTables() {
        return animeDatabase.clearAllTables()
    }

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(context: Context): LocalDataSource {
            synchronized(this) {
                return INSTANCE ?: LocalDataSource().also { instance ->
                    instance.animeDatabase = AnimeDatabase.getInstance(context)
                    INSTANCE = instance
                }
            }
        }
    }
}
