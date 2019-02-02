package com.yossisegev.movienight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.common.DomainTestUtils
import com.yossisegev.domain.common.TestTransformer
import com.yossisegev.domain.usecases.GetFavoriteMovies
import com.yossisegev.movienight.favorites.FavoriteMoviesViewModel
import com.yossisegev.movienight.favorites.FavoritesMoviesViewState
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.ClassRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class FavoriteMoviesViewModelTests {

    companion object {
        @ClassRule @JvmField
        var schedulers = RxImmediateSchedulerRule()
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val movieEntityMovieMapper = MovieEntityMovieMapper()
    private lateinit var favoriteMoviesViewModel: FavoriteMoviesViewModel
    private lateinit var moviesCache: MoviesCache
    private lateinit var viewObserver: Observer<FavoritesMoviesViewState>
    private lateinit var errorObserver: Observer<Throwable?>

    @Before
    fun before() {
        moviesCache = mock(MoviesCache::class.java)
        val getFavoriteMovies = GetFavoriteMovies(TestTransformer(), moviesCache)
        favoriteMoviesViewModel = FavoriteMoviesViewModel(getFavoriteMovies, movieEntityMovieMapper)
        viewObserver = mock(Observer::class.java) as Observer<FavoritesMoviesViewState>
        errorObserver = mock(Observer::class.java) as Observer<Throwable?>
        favoriteMoviesViewModel.viewState.observeForever(viewObserver)
        favoriteMoviesViewModel.errorState.observeForever(errorObserver)
    }

    @Test
    fun testInitialViewStateShowsLoading() {
        verify(viewObserver).onChanged(FavoritesMoviesViewState(isLoading = true, isEmpty = true, movies = null))
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testShowingMoviesAsExpectedAndStopsLoading() {
        val movieEntities = DomainTestUtils.generateMovieEntityList()
        `when`(moviesCache.getAll()).thenReturn(Single.just(movieEntities))
        val movies = movieEntities.map { movieEntityMovieMapper.mapFrom(it) }
        favoriteMoviesViewModel.getFavorites()

        verify(viewObserver).onChanged(FavoritesMoviesViewState(isLoading = false, isEmpty = false, movies = movies))
        verify(errorObserver).onChanged(null)
    }

    @Test
    fun testShowingEmptyMessage() {
        `when`(moviesCache.getAll()).thenReturn(Single.just(mutableListOf()))
        favoriteMoviesViewModel.getFavorites()

        verify(viewObserver).onChanged(FavoritesMoviesViewState(isLoading = false, isEmpty = true, movies = mutableListOf()))
        verify(errorObserver).onChanged(null)
    }

    @Test
    fun testShowingErrorWhenNeeded() {
        val throwable = Throwable("ERROR!")
        `when`(moviesCache.getAll()).thenReturn(Single.error(throwable))
        favoriteMoviesViewModel.getFavorites()

        verify(viewObserver).onChanged(FavoritesMoviesViewState(isLoading = false, isEmpty = false, movies = null))
        verify(errorObserver).onChanged(throwable)
    }
}
