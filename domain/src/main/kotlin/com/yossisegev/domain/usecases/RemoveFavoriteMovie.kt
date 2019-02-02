package com.yossisegev.domain.usecases

import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.common.Transformer
import com.yossisegev.domain.entities.MovieEntity
import io.reactivex.Single
import java.lang.IllegalArgumentException

/**
 * Created by Yossi Segev on 21/01/2018.
 */
class RemoveFavoriteMovie(transformer:Transformer<Boolean>,
                          private val moviesCache: MoviesCache): UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_MOVIE_ENTITY = "param:movieEntity"
    }

    fun remove(movieEntity: MovieEntity): Single<Boolean> {
        val data = HashMap<String, MovieEntity>()
        data[PARAM_MOVIE_ENTITY] = movieEntity
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Single<Boolean> {

        val movieEntity = data?.get(PARAM_MOVIE_ENTITY)

        movieEntity?.let {
            return Single.fromCallable {
                val entity = it as MovieEntity
                moviesCache.remove(entity)
                return@fromCallable false
            }
        }?: return Single.error({ IllegalArgumentException("MovieEntity must be provided.") })
    }

}