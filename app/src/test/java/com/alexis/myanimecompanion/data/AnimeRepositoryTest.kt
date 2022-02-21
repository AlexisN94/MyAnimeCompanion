package com.alexis.myanimecompanion.data

import com.alexis.myanimecompanion.ReflectionUtils
import com.alexis.myanimecompanion.data.remote.models.MainPicture
import com.alexis.myanimecompanion.data.remote.models.Node
import com.alexis.myanimecompanion.data.remote.models.SearchResult
import com.alexis.myanimecompanion.data.remote.models.SearchResultData
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class AnimeRepositoryTest {
    lateinit var repository: AnimeRepository
    lateinit var localDataSource: LocalDataSource
    lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        repository = ReflectionUtils.invokeConstructor(AnimeRepository::class)

        localDataSource = mock(LocalDataSource::class.java)
        remoteDataSource = mock(RemoteDataSource::class.java)

        ReflectionUtils.setField(repository, "localDataSource", localDataSource)
        ReflectionUtils.setField(repository, "remoteDataSource", remoteDataSource)
    }

    @Test
    fun `search() returns error on failure`() = runTest {
        val expectedError = Error.Authorization

        `when`(remoteDataSource.trySearch(anyString(), anyInt(), anyInt(), anyString()))
            .thenReturn(Result.failure(expectedError))

        val searchResult = repository.search("", 100, 0)

        assertEquals("search() returns success on failure!?", true, searchResult.isFailure)
        assertEquals(expectedError, searchResult.errorOrNull())
    }

    @Test
    fun `search() returns listOfAnime on success`() = runTest {
        `when`(remoteDataSource.trySearch(anyString(), anyInt(), anyInt(), anyString()))
            .thenReturn(
                Result.success(
                    SearchResult(
                        listOf(
                            SearchResultData(
                                Node(0, "Anime1", MainPicture("urlL", "urlM"))
                            )
                        )
                    )
                )
            )

        val actualResult = repository.search("query", 100, 0)

        assertEquals("search() returns failure on success!?", true, actualResult.isSuccess)
        val expectedResult = listOf(Anime(0, "Anime1", "urlL"))
        assertEquals(expectedResult, actualResult.getOrNull())
    }

    @Test
    fun `logout() clears data`() {
        repository.logout()

        verify(localDataSource).clearAllTables()
        verify(remoteDataSource).clearUser()
    }

    @Test
    fun `isLoggedIn() returns true`() = runTest {
        `when`(remoteDataSource.hasValidToken()).thenReturn(true)

        val isLoggedIn = repository.isLoggedIn()

        assertEquals(true, isLoggedIn)
    }
}
