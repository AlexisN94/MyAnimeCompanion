package com.alexis.myanimecompanion.data

import androidx.room.Room
import com.alexis.myanimecompanion.MockUtils
import com.alexis.myanimecompanion.ReflectionUtils
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.models.DatabaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LocalDataSourceTest {
    lateinit var animeDatabase: AnimeDatabase
    lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        val context = RuntimeEnvironment.getApplication().applicationContext
        animeDatabase = Room.inMemoryDatabaseBuilder(context, AnimeDatabase::class.java).build()

        localDataSource = ReflectionUtils.invokeConstructor(LocalDataSource::class)

        ReflectionUtils.setField(localDataSource, "animeDatabase", animeDatabase)
    }

    @After
    fun closeDb() {
        animeDatabase.close()
    }

    @Test
    fun testInsertAndReadUser() = runTest {
        val user = withContext(Dispatchers.IO) {
            localDataSource.insertOrUpdateUser(DatabaseUser(id = 1))
            localDataSource.getUser()
        }

        assertEquals(DatabaseUser(id = 1), user)
    }

    @Test
    fun testReadNullUser() = runTest {
        val user = withContext(Dispatchers.IO) {
            localDataSource.getUser()
        }
        assertNull(user)
    }

    @Test
    fun testInsertAndReadAnime() = runTest {
        val completeDatabaseAnime = MockUtils.mockDatabaseCompleteAnime()

        val result = withContext(Dispatchers.IO) {
            localDataSource.insertOrUpdateAnime(completeDatabaseAnime)
            localDataSource.getAnime(0)
        }

        assertEquals(completeDatabaseAnime, result)
    }

    @Test
    fun testInsertAnimeList() = runTest {
        val animeList = MockUtils.mockDatabaseAnimeList()

        val result = withContext(Dispatchers.IO) {
            localDataSource.insertOrUpdateAnimeList(animeList)
            localDataSource.getAnimeList()
        }

        result.observeForever {
            assertEquals(animeList, it)
        }
    }
}
