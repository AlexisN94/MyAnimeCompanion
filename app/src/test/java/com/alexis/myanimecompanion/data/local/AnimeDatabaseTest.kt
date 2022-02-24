package com.alexis.myanimecompanion.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.alexis.myanimecompanion.data.local.daos.AnimeDao
import com.alexis.myanimecompanion.data.local.daos.AnimeDetailsDao
import com.alexis.myanimecompanion.data.local.daos.AnimeStatusDao
import com.alexis.myanimecompanion.data.local.daos.UserDao
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class AnimeDatabaseTest {
    lateinit var db: AnimeDatabase
    lateinit var userDao: UserDao
    lateinit var animeDao: AnimeDao
    lateinit var animeDetailsDao: AnimeDetailsDao
    lateinit var animeStatusDao: AnimeStatusDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context, AnimeDatabase::class.java
        ).build()

        userDao = db.userDao
        animeDao = db.animeDao
        animeStatusDao = db.animeStatusDao
        animeDetailsDao = db.animeDetailsDao

    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndReadUser() = runTest {
        val user = DatabaseUser(id = -1, username = "testUser")

        val result = withContext(Dispatchers.IO) {
            userDao.insert(user)
            return@withContext userDao.getUser()
        }

        assertEquals(user, result)

    }

    @Test
    fun testDeleteUser() = runTest {
        val user = DatabaseUser(id = -1, username = "testUser")

        val result = withContext(Dispatchers.IO) {
            userDao.insert(user)
            userDao.delete(user)
            return@withContext userDao.getUser()
        }

        assertNull(result)
    }

    @Test
    fun testUpdateUser() = runTest {
        val user = DatabaseUser(id = -1, username = "testUser")
        val updatedUser = DatabaseUser(id = -1, username = "updatedTestUser")

        val result = withContext(Dispatchers.IO) {
            userDao.insert(user)
            userDao.update(updatedUser)
            return@withContext userDao.getUser()
        }

        assertEquals(updatedUser, result)
    }

    @Test
    fun testInsertAndReadAnime() = runTest {
        val databaseAnime = DatabaseAnime(0, "", "")

        val result = withContext(Dispatchers.IO) {
            animeDao.insert(databaseAnime)
            animeDao.getById(0) // returns DatabaseCompleteAnime. getting just DatabaseAnime is not implemented, so test fails
        }

        assertEquals(databaseAnime, result)
    }
}
