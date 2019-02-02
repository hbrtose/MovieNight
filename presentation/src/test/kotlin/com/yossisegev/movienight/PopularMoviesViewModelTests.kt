package com.yossisegev.movienight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.yossisegev.domain.MoviesRepository
import com.yossisegev.domain.common.DomainTestUtils
import com.yossisegev.domain.common.TestTransformer
import com.yossisegev.domain.usecases.GetPopularMovies
import com.yossisegev.movienight.popularmovies.PopularMoviesViewModel
import com.yossisegev.movienight.popularmovies.PopularMoviesViewState
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@Suppress("UNCHECKED_CAST")
@RunWith(MockitoJUnitRunner::class)
class PopularMoviesViewModelTests {

    companion object {
        @ClassRule
        @JvmField
        var schedulers = RxImmediateSchedulerRule()
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()


    private val movieEntityMovieMapper = MovieEntityMovieMapper()
    private lateinit var popularMoviesViewModel: PopularMoviesViewModel
    private lateinit var moviesRepository: MoviesRepository
    private lateinit var viewObserver: Observer<PopularMoviesViewState>
    private lateinit var errorObserver: Observer<Throwable?>

    @Before
    fun before() {
        moviesRepository = Mockito.mock(MoviesRepository::class.java)
        val getPopularMoviesUseCase = GetPopularMovies(TestTransformer(), moviesRepository)
        popularMoviesViewModel = PopularMoviesViewModel(getPopularMoviesUseCase, movieEntityMovieMapper)
        viewObserver = mock(Observer::class.java) as Observer<PopularMoviesViewState>
        errorObserver = mock(Observer::class.java) as Observer<Throwable?>
        popularMoviesViewModel.viewState.observeForever(viewObserver)
        popularMoviesViewModel.errorState.observeForever(errorObserver)
    }

    @Test
    fun testInitialViewStateShowsLoading() {
        verify(viewObserver).onChanged(PopularMoviesViewState(showLoading = true, movies = null))
        verifyZeroInteractions(viewObserver)
    }

    @Test
    fun testShowingMoviesAsExpectedAndStopsLoading() {
        val movieEntities = DomainTestUtils.generateMovieEntityList()
        `when`(moviesRepository.getMovies()).thenReturn(Single.just(movieEntities))
        popularMoviesViewModel.getPopularMovies()
        val movies = movieEntities.map { movieEntityMovieMapper.mapFrom(it) }

        verify(viewObserver).onChanged(PopularMoviesViewState(showLoading = false, movies = movies))
        verify(errorObserver).onChanged(null)
    }

    @Test
    fun testShowingErrorMessageWhenNeeded() {
        val throwable = Throwable("ERROR!")
        `when`(moviesRepository.getMovies()).thenReturn(Single.error(throwable))
        popularMoviesViewModel.getPopularMovies()

        verify(viewObserver).onChanged(PopularMoviesViewState(showLoading = false, movies = null))
        verify(errorObserver).onChanged(throwable)
    }
}
