package com.alexis.myanimecompanion.data.local.daos

import androidx.room.*
import com.alexis.myanimecompanion.data.local.models.DatabaseUser

@Dao
interface UserDao {
    @Insert
    fun insert(databaseUser: DatabaseUser)

    @Update
    fun update(databaseUser: DatabaseUser)

    @Delete
    fun delete(databaseUser: DatabaseUser)

    @Query("SELECT * FROM DatabaseUser WHERE id = :userId")
    fun getUserById(userId: Int) : DatabaseUser
}
