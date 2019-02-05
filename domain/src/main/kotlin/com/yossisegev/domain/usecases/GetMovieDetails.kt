package com.yossisegev.domain.usecases

import com.yossisegev.domain.MoviesRepository
import com.yossisegev.domain.common.Transformer
import com.yossisegev.domain.entities.MovieEntity
import com.yossisegev.domain.entities.Optional
import io.reactivex.Single
import java.lang.IllegalArgumentException

/**
 * Created by Yossi Segev on 11/11/2017.
 */
class GetMovieDetails(
        transformer: Transformer<Optional<MovieEntity>>,
        private val moviesRepository: MoviesRepository) : UseCase<Int, Optional<MovieEntity>>(transformer) {

    fun getById(movieId: Int): Single<Optional<MovieEntity>> {
        return observable(movieId)
    }

    override fun createObservable(data: Int?): Single<Optional<MovieEntity>> {
        data?.let {
            return moviesRepository.getMovie(it)
        } ?: return Single.error({ IllegalArgumentException("MovieId must be provided.") })
    }
}