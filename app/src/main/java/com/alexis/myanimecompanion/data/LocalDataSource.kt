package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeWithStatus
import com.alexis.myanimecompanion.data.local.models.DatabaseUser

class LocalDataSource private constructor() {
    private lateinit var animeDatabase: AnimeDatabase

    fun insertOrUpdateUser(user: DatabaseUser) {
        if (updateUser(user) == 0) {
            insertUser(user)
        }
    }

    fun getUser(): DatabaseUser? {
        return animeDatabase.userDao.getUser()
    }

    fun insertOrUpdateAnime(animeWithStatus: DatabaseAnimeWithStatus) {
        val anime = animeWithStatus.anime
        val status = animeWithStatus.animeStatus

        if (animeDatabase.animeDao.update(anime) == 0) {
            animeDatabase.animeDao.insert(anime)
        }
        if (animeDatabase.animeStatusDao.update(status) == 0) {
            animeDatabase.animeStatusDao.insert(status)
        }
    }

    fun getAnime(animeId: Int): DatabaseAnimeWithStatus {
        return animeDatabase.animeDao.getById(animeId)
    }

    fun deleteAnime(anime: DatabaseAnime) {
        animeDatabase.animeDao.delete(anime)
    }

    fun getAnimeList(): List<DatabaseAnimeWithStatus> {
        return animeDatabase.animeDao.getAll()
    }

    fun insertOrUpdateAnimeList(animeList: List<DatabaseAnimeWithStatus>) {
        val animeArray = animeList.map { it.anime }.toTypedArray()
        val statusArray = animeList.map { it.animeStatus }.toTypedArray()

        animeDatabase.animeDao.insertAll(*animeArray)
        animeDatabase.animeStatusDao.insertAll(*statusArray)
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
