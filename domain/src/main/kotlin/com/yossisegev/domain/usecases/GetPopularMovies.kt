package com.yossisegev.domain.usecases

import com.yossisegev.domain.entities.MovieEntity
import com.yossisegev.domain.MoviesRepository
import com.yossisegev.domain.common.Transformer
import io.reactivex.Single

/**
 * Created by Yossi Segev on 11/11/2017.
 */
open class GetPopularMovies(transformer: Transformer<List<MovieEntity>>,
                            private val moviesRepository: MoviesRepository) : UseCase<Any, List<MovieEntity>>(transformer) {
    override fun createObservable(data: Any?): Single<List<MovieEntity>> {
        return moviesRepository.getMovies()
    }

}