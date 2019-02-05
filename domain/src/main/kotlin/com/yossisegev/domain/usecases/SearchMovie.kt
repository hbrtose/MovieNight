package com.yossisegev.domain.usecases

import com.yossisegev.domain.MoviesRepository
import com.yossisegev.domain.common.Transformer
import com.yossisegev.domain.entities.MovieEntity
import io.reactivex.Single

/**
 * Created by Yossi Segev on 11/02/2018.
 */
class SearchMovie(transformer: Transformer<List<MovieEntity>>,
                  private val moviesRepository: MoviesRepository) : UseCase<String, List<MovieEntity>>(transformer) {

    fun search(query: String): Single<List<MovieEntity>> {
        return observable(query)
    }

    override fun createObservable(data: String?): Single<List<MovieEntity>> {
        data?.let {
            return moviesRepository.search(it)
        } ?: return Single.just(emptyList())
    }

}