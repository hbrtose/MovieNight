package com.yossisegev.domain.usecases

import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.common.Transformer
import com.yossisegev.domain.entities.MovieEntity
import io.reactivex.Single
import java.lang.IllegalArgumentException

/**
 * Created by Yossi Segev on 21/01/2018.
 */
class SaveFavoriteMovie(transformer: Transformer<Boolean>,
                        private val moviesCache: MoviesCache): UseCase<MovieEntity, Boolean>(transformer) {

    override fun createObservable(data: MovieEntity?): Single<Boolean> {

        data?.let {
            return Single.fromCallable {
                val entity = it
                moviesCache.save(entity)
                return@fromCallable true
            }
        }?: return Single.error({ IllegalArgumentException("MovieEntity must be provided.") })

    }

    fun save(movieEntity: MovieEntity): Single<Boolean> {
        return observable(movieEntity)
    }
}