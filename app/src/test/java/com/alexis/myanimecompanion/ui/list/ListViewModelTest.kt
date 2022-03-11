package com.alexis.myanimecompanion.ui.list

import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.data.Result
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.testutils.MockUtils.anyObject
import com.alexis.myanimecompanion.testutils.MockUtils.mockAnime
import com.alexis.myanimecompanion.testutils.capture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

enum class AnimeStatus(val value: String) {
    FINISHED_AIRING("finished_airing"),
    WATCHED("completed"),
    WATCHING("watching"),
    PLAN_TO_WATCH("plan_to_watch")
}

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class ListViewModelTest {
    @Captor lateinit var animeCaptor: ArgumentCaptor<Anime>
    @Mock lateinit var animeRepository: AnimeRepository
    @Mock lateinit var resources: Resources
    private lateinit var listViewModel: ListViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() = runTest {
        `when`(animeRepository.getAnimeList()).thenReturn(MutableLiveData(listOf(mockAnime())))

        Dispatchers.setMain(testDispatcher)
        listViewModel = ListViewModel(animeRepository, resources, testDispatcher)
        reset(animeRepository, resources)
    }

    @After
    fun close() {
        Dispatchers.resetMain()
    }

    @Test
    fun testRefreshAnimeList() = runTest {
        `when`(animeRepository.refreshAnimeList()).thenReturn(Result.failure(Error.Network))

        listViewModel.refreshAnimeList()

        verify(animeRepository).refreshAnimeList()
        assertEquals(Error.Network.name, listViewModel.statusMessage.value)
    }

    @Test
    fun testEditWatchedEpisodes_shouldNot_decrementBelowZero() = runTest {
        listViewModel.editWatchedEpisodes(mockAnime(), ListViewModel.WatchedEpisodesEditType.DECREMENT)

        verify(animeRepository, times(0)).updateAnimeStatus(anyObject())
    }

    @Test
    fun testEditWatchedEpisodes_shouldNot_incrementAboveTotalEps() = runTest {
        `when`(animeRepository.updateAnimeStatus(anyObject())).thenReturn(Result.failure(Error.Network))

        listViewModel.editWatchedEpisodes(
            mockAnime().also {
                it.details!!.numEpisodes = 5
                it.myListStatus!!.episodesWatched = 5
            },
            ListViewModel.WatchedEpisodesEditType.INCREMENT
        )

        verify(animeRepository, times(0)).updateAnimeStatus(anyObject())
    }

    @Test
    fun testEditWatchedEpisodes_should_incrementByOne() = runTest {
        `when`(animeRepository.updateAnimeStatus(anyObject())).thenReturn(Result.failure(Error.Network))

        val anime = mockAnime()

        listViewModel.editWatchedEpisodes(
            anime.also {
                it.details!!.numEpisodes = 5
                it.myListStatus!!.episodesWatched = 3
            },
            ListViewModel.WatchedEpisodesEditType.INCREMENT
        )

        verify(animeRepository).updateAnimeStatus(
            anime.also {
                it.details!!.numEpisodes = 5
                it.myListStatus!!.episodesWatched = 2 // should only pass when 4
            }
        )
    }

    @Test
    fun testEditWatchedEpisodes_should_decrementByOne() = runTest {
        `when`(animeRepository.updateAnimeStatus(anyObject())).thenReturn(Result.success())

        val anime = mockAnime().apply {
            details!!.numEpisodes = 5
            myListStatus!!.episodesWatched = 3
        }

        listViewModel.editWatchedEpisodes(anime, ListViewModel.WatchedEpisodesEditType.DECREMENT)

        val expectedResult = anime.apply {
            details!!.numEpisodes = 5
            myListStatus!!.episodesWatched = 1 // should only pass when 2
        }

        lateinit var actualResult: Anime
        verify(animeRepository).updateAnimeStatus(capture(animeCaptor).also { actualResult = it })

        assertEquals(expectedResult.myListStatus!!.episodesWatched, animeCaptor.value.myListStatus!!.episodesWatched)
    }

    @Test
    fun testEditWatchedEpisodes_should_changeStatusToFinished() = runTest {
        `when`(animeRepository.updateAnimeStatus(anyObject())).thenReturn(Result.failure(Error.Network))

        val anime = mockAnime()

        listViewModel.editWatchedEpisodes(
            anime.apply {
                details!!.numEpisodes = 5
                details!!.status = AnimeStatus.FINISHED_AIRING.value
                myListStatus!!.episodesWatched = 4
                myListStatus!!.status = AnimeStatus.WATCHING.value
            },
            ListViewModel.WatchedEpisodesEditType.INCREMENT
        )

        verify(animeRepository).updateAnimeStatus(
            anime.apply {
                myListStatus!!.episodesWatched = 5
                myListStatus!!.status = AnimeStatus.WATCHED.value
            }
        )
    }
}
