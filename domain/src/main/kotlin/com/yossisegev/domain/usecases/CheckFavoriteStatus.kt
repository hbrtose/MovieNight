package com.yossisegev.domain.usecases

import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.common.Transformer
import io.reactivex.Single
import java.lang.IllegalArgumentException

/**
 * Created by Yossi Segev on 09/02/2018.
 */

class CheckFavoriteStatus(transformer: Transformer<Boolean>,
                          private val moviesCache: MoviesCache) : UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_MOVIE_ENTITY = "param:movieEntity"
    }

    override fun createObservable(data: Map<String, Any>?): Single<Boolean> {
        val movieId = data?.get(PARAM_MOVIE_ENTITY)
        movieId?.let {
            return moviesCache.get(it as Int).flatMap { optionalMovieEntity ->
                return@flatMap Single.just(optionalMovieEntity.hasValue())
            }
        } ?: return Single.error({ IllegalArgumentException("MovieId must be provided.") })
    }

    fun check(movieId: Int): Single<Boolean> {
        val data = HashMap<String, Int>()
        data[PARAM_MOVIE_ENTITY] = movieId
        return observable(data)
    }

}