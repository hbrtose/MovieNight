package com.yossisegev.movienight

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.MoviesRepository
import com.yossisegev.domain.common.DomainTestUtils
import com.yossisegev.domain.common.TestTransformer
import com.yossisegev.domain.entities.Optional
import com.yossisegev.domain.usecases.CheckFavoriteStatus
import com.yossisegev.domain.usecases.GetMovieDetails
import com.yossisegev.domain.usecases.RemoveFavoriteMovie
import com.yossisegev.domain.usecases.SaveFavoriteMovie
import com.yossisegev.movienight.details.MovieDetailsViewModel
import com.yossisegev.movienight.details.MovieDetailsViewState
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by Yossi Segev on 19/02/2018.
 */

@Suppress("UNCHECKED_CAST")
@RunWith(MockitoJUnitRunner::class)
class MovieDetailsViewModelTests {

    companion object {
        @ClassRule
        @JvmField
        var schedulers = RxImmediateSchedulerRule()
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()


    private val testMovieId = 100
    private val movieEntityMovieMapper = MovieEntityMovieMapper()
    private lateinit var movieDetailsViewModel: MovieDetailsViewModel
    private lateinit var moviesRepository: MoviesRepository
    private lateinit var moviesCache: MoviesCache
    private lateinit var viewObserver: Observer<MovieDetailsViewState>
    private lateinit var errorObserver: Observer<Throwable>
    private lateinit var favoriteStateObserver: Observer<Boolean>

    @Before
    fun before() {
        moviesRepository = mock(MoviesRepository::class.java)
        moviesCache = mock(MoviesCache::class.java)
        val getMovieDetails = GetMovieDetails(TestTransformer(), moviesRepository)
        val saveFavoriteMovie = SaveFavoriteMovie(TestTransformer(), moviesCache)
        val removeFavoriteMovie = RemoveFavoriteMovie(TestTransformer(), moviesCache)
        val checkFavoriteStatus = CheckFavoriteStatus(TestTransformer(), moviesCache)
        movieDetailsViewModel = MovieDetailsViewModel(
                getMovieDetails,
                saveFavoriteMovie,
                removeFavoriteMovie,
                checkFavoriteStatus,
                movieEntityMovieMapper)

        viewObserver = mock(Observer::class.java) as Observer<MovieDetailsViewState>
        favoriteStateObserver = mock(Observer::class.java) as Observer<Boolean>
        errorObserver = mock(Observer::class.java) as Observer<Throwable>
        movieDetailsViewModel.viewState.observeForever(viewObserver)
        movieDetailsViewModel.errorState.observeForever(errorObserver)
        movieDetailsViewModel.favoriteState.observeForever(favoriteStateObserver)
    }

    @Test
    fun showsCorrectDetailsAndFavoriteState() {
        val movieEntity = DomainTestUtils.getTestMovieEntity(testMovieId)
        `when`(moviesRepository.getMovie(testMovieId)).thenReturn(Single.just(
                Optional.of(movieEntity)
        ))
        `when`(moviesCache.get(testMovieId)).thenReturn(Single.just(Optional.of(movieEntity)))

        movieDetailsViewModel.getMovieDetails(testMovieId)

        val video = movieEntityMovieMapper.mapFrom(movieEntity)
        val expectedDetailsViewState = MovieDetailsViewState(
                isLoading = false,
                title = video.title,
                overview = video.details?.overview,
                videos = video.details?.videos,
                homepage = video.details?.homepage,
                releaseDate = video.releaseDate,
                backdropUrl = video.backdropPath,
                votesAverage = video.voteAverage,
                genres = video.details?.genres)

        verify(viewObserver).onChanged(expectedDetailsViewState)
        verify(favoriteStateObserver).onChanged(true)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun showsErrorWhenFailsToGetMovieFromRepository() {
        val movieEntity = DomainTestUtils.getTestMovieEntity(testMovieId)
        val throwable = Throwable("ERROR!")

        `when`(moviesRepository.getMovie(testMovieId)).thenReturn(Single.error(throwable))
        `when`(moviesCache.get(testMovieId)).thenReturn(Single.just(Optional.of(movieEntity)))

        movieDetailsViewModel.getMovieDetails(testMovieId)

        verify(errorObserver).onChanged(throwable)
        verifyZeroInteractions(favoriteStateObserver)
    }

    @Test
    fun showsErrorWhenFailsToGetFavoriteState() {
        val movieEntity = DomainTestUtils.getTestMovieEntity(testMovieId)

        `when`(moviesRepository.getMovie(testMovieId)).thenReturn(Single.just(Optional.empty()))
        `when`(moviesCache.get(testMovieId)).thenReturn(Single.just(Optional.of(movieEntity)))

        movieDetailsViewModel.getMovieDetails(testMovieId)

        verify(errorObserver).onChanged(any(Throwable::class.java))
    }

    @Test
    fun showsErrorWhenGetMovieFromRepositoryReturnsEmptyOptional() {
        val movieEntity = DomainTestUtils.getTestMovieEntity(testMovieId)
        val throwable = Throwable("ERROR!")

        `when`(moviesRepository.getMovie(testMovieId)).thenReturn(Single.just(Optional.of(movieEntity)))
        `when`(moviesCache.get(testMovieId)).thenReturn(Single.error(throwable))

        movieDetailsViewModel.getMovieDetails(testMovieId)

        verify(errorObserver).onChanged(throwable)
        verifyZeroInteractions(favoriteStateObserver)
    }

    @Test
    fun favoriteStateChangesAsExpected() {
        val movieEntity = DomainTestUtils.getTestMovieEntity(testMovieId)
        `when`(moviesRepository.getMovie(testMovieId)).thenReturn(Single.just(
                Optional.of(movieEntity)
        ))

        `when`(moviesCache.get(testMovieId)).thenReturn(Single.just(Optional.of(movieEntity)))

        movieDetailsViewModel.getMovieDetails(testMovieId)
        verify(favoriteStateObserver).onChanged(true)

        movieDetailsViewModel.favoriteButtonClicked(testMovieId)
        verify(favoriteStateObserver).onChanged(false)

        movieDetailsViewModel.favoriteButtonClicked(testMovieId)
        verify(favoriteStateObserver).onChanged(true)

        verifyZeroInteractions(errorObserver)
    }


}