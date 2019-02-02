package com.yossisegev.movienight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.yossisegev.domain.MoviesRepository
import com.yossisegev.domain.common.DomainTestUtils
import com.yossisegev.domain.common.TestTransformer
import com.yossisegev.domain.usecases.SearchMovie
import com.yossisegev.movienight.search.SearchViewModel
import com.yossisegev.movienight.search.SearchViewState
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@Suppress("UNCHECKED_CAST")
@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTests {

    companion object {
        @ClassRule
        @JvmField
        var schedulers = RxImmediateSchedulerRule()
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()


    private val testQuery = "this is a test query"
    private val movieEntityMovieMapper = MovieEntityMovieMapper()
    private lateinit var movieRepository: MoviesRepository
    private lateinit var searchViewModel: SearchViewModel

    @Before
    fun before() {
        movieRepository = mock(MoviesRepository::class.java)
        val searchMovie = SearchMovie(TestTransformer(), movieRepository)
        searchViewModel = SearchViewModel(searchMovie, movieEntityMovieMapper)
    }

    @Test
    fun testInitialViewState() {
        val viewObserver = mock(Observer::class.java) as Observer<SearchViewState>
        val errorObserver = mock(Observer::class.java) as Observer<Throwable?>
        searchViewModel.viewState.observeForever(viewObserver)
        searchViewModel.errorState.observeForever(errorObserver)
        verify(viewObserver).onChanged(SearchViewState(
                isLoading = false,
                movies = null,
                showNoResultsMessage = false,
                lastSearchedQuery = null))

        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testSearchWithResultsShowsCorrectViewStates() {

        val movieEntities = DomainTestUtils.generateMovieEntityList()
        `when`(movieRepository.search(testQuery)).thenReturn(Single.just(movieEntities))
        val viewObserver = ChangeHistoryObserver<SearchViewState>()
        val errorObserver = mock(Observer::class.java) as Observer<Throwable?>
        searchViewModel.viewState.observeForever(viewObserver)
        searchViewModel.errorState.observeForever(errorObserver)
        searchViewModel.search(testQuery)

        val loadingViewState = SearchViewState(
                isLoading = true,
                showNoResultsMessage = false,
                movies = null)

        val resultsViewState = SearchViewState(
                isLoading = false,
                lastSearchedQuery = testQuery,
                movies = movieEntities.map { movieEntityMovieMapper.mapFrom(it) }
        )
        assertEquals(viewObserver.get(1), loadingViewState)
        assertEquals(viewObserver.get(2), resultsViewState)
        verify(errorObserver).onChanged(null)
    }

    @Test
    fun testSearchWithNoResultsShowsCorrectViewStates() {

        `when`(movieRepository.search(testQuery)).thenReturn(Single.just(emptyList()))
        val viewObserver = ChangeHistoryObserver<SearchViewState>()
        val errorObserver = mock(Observer::class.java) as Observer<Throwable?>
        searchViewModel.viewState.observeForever(viewObserver)
        searchViewModel.errorState.observeForever(errorObserver)
        searchViewModel.search(testQuery)

        val loadingViewState = SearchViewState(
                isLoading = true,
                showNoResultsMessage = false,
                lastSearchedQuery = null,
                movies = null)

        val resultsViewState = SearchViewState(
                isLoading = false,
                lastSearchedQuery = testQuery,
                showNoResultsMessage = true,
                movies = emptyList()
        )
        assertEquals(viewObserver.get(1), loadingViewState)
        assertEquals(viewObserver.get(2), resultsViewState)
        verify(errorObserver).onChanged(null)
    }

    @Test
    fun testQueryStringIsEmptyShowsCorrectViewStates() {

        //`when`(movieRepository.search(testQuery)).thenReturn(Single.just(emptyList()))
        val viewObserver = ChangeHistoryObserver<SearchViewState>()
        val errorObserver = mock(Observer::class.java) as Observer<Throwable?>
        searchViewModel.viewState.observeForever(viewObserver)
        searchViewModel.errorState.observeForever(errorObserver)
        searchViewModel.search("")

        val loadingViewState = SearchViewState(
                isLoading = false,
                showNoResultsMessage = false,
                lastSearchedQuery = "",
                movies = null)
        assertEquals(viewObserver.get(1), loadingViewState)
        verify(errorObserver).onChanged(null)
    }

    @Test
    fun testErrorShowsCorrectViewStates() {
        val throwable = Throwable("ERROR!")
        `when`(movieRepository.search(testQuery)).thenReturn(Single.error(throwable))
        val viewObserver = ChangeHistoryObserver<SearchViewState>()
        val errorObserver = mock(Observer::class.java) as Observer<Throwable?>
        searchViewModel.viewState.observeForever(viewObserver)
        searchViewModel.errorState.observeForever(errorObserver)
        searchViewModel.search(testQuery)

        val loadingViewState = SearchViewState(
                isLoading = false,
                showNoResultsMessage = false,
                lastSearchedQuery = testQuery,
                movies = null)
        assertEquals(viewObserver.get(2), loadingViewState)
        verify(errorObserver).onChanged(throwable)
    }
}
