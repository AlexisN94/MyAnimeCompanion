package com.alexis.myanimecompanion.data.local.daos

import androidx.room.*
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeWithStatus

@Dao
interface AnimeDao {
    @Insert
    fun insert(databaseAnime: DatabaseAnime)

    @Update
    fun update(databaseAnime: DatabaseAnime): Int

    @Delete
    fun delete(databaseAnime: DatabaseAnime)

    @Query("SELECT * FROM DatabaseAnime WHERE id = :animeId")
    fun getById(animeId: Int): DatabaseAnimeWithStatus

    @Transaction
    @Insert
    fun insertAll(animeList: List<DatabaseAnimeWithStatus>)

    @Transaction
    @Query("SELECT * FROM DatabaseAnime")
    fun getAll(): List<DatabaseAnimeWithStatus>

}
