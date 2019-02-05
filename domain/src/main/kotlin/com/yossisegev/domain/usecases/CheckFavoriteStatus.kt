package com.yossisegev.domain.usecases

import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.common.Transformer
import io.reactivex.Single
import java.lang.IllegalArgumentException

/**
 * Created by Yossi Segev on 09/02/2018.
 */

class CheckFavoriteStatus(transformer: Transformer<Boolean>,
                          private val moviesCache: MoviesCache) : UseCase<Int, Boolean>(transformer) {

    override fun createObservable(data: Int?): Single<Boolean> {
        data?.let {
            return moviesCache.get(it).flatMap { optionalMovieEntity ->
                return@flatMap Single.just(optionalMovieEntity.hasValue())
            }
        } ?: return Single.error({ IllegalArgumentException("MovieId must be provided.") })
    }

    fun check(movieId: Int): Single<Boolean> {
        return observable(movieId)
    }

}