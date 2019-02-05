package com.yossisegev.domain.usecases

import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.common.Transformer
import com.yossisegev.domain.entities.MovieEntity
import io.reactivex.Single

/**
 * Created by Yossi Segev on 21/01/2018.
 */
class GetFavoriteMovies(transformer: Transformer<List<MovieEntity>>,
                        private val moviesCache: MoviesCache): UseCase<Any, List<MovieEntity>>(transformer) {

    override fun createObservable(data: Any?): Single<List<MovieEntity>> {
        return moviesCache.getAll()
    }

}