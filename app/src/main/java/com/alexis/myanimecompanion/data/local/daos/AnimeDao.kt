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

    @Transaction
    @Query("SELECT * FROM DatabaseAnime WHERE id = :animeId")
    fun getById(animeId: Int): DatabaseAnimeWithStatus

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg animes: DatabaseAnime)

    @Transaction
    @Query("SELECT * FROM DatabaseAnime")
    fun getAll(): List<DatabaseAnimeWithStatus>

}
