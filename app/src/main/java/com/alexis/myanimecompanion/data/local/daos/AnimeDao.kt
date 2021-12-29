package com.alexis.myanimecompanion.data.local.daos

import androidx.room.*
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime

@Dao
interface AnimeDao {
    @Insert
    fun insert(databaseAnime: DatabaseAnime)

    @Update
    fun update(databaseAnime: DatabaseAnime)

    @Delete
    fun delete(databaseAnime: DatabaseAnime)

    @Query("SELECT * FROM DatabaseAnime WHERE id = :animeId")
    fun getById(animeId: Int)
}
