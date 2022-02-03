package com.alexis.myanimecompanion.data.local.daos

import androidx.room.*
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeDetails

@Dao
interface AnimeDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(animeDetails: DatabaseAnimeDetails)

    @Update
    fun update(animeDetails: DatabaseAnimeDetails): Int

    @Delete
    fun delete(animeDetails: DatabaseAnimeDetails)

    @Query("SELECT * FROM DatabaseAnimeDetails WHERE animeId = :animeId")
    fun getDetailsByAnimeId(animeId: Int): DatabaseAnimeDetails

    @Query("DELETE FROM DatabaseAnimeDetails WHERE animeId = :animeId")
    fun deleteByAnimeId(animeId: Int)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg animeStatus: DatabaseAnimeDetails)
}
