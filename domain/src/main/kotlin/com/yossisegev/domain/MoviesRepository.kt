package com.yossisegev.domain

import com.yossisegev.domain.entities.MovieEntity
import com.yossisegev.domain.entities.Optional
import io.reactivex.Single

/**
 * Created by Yossi Segev on 25/01/2018.
 */
interface MoviesRepository {
    fun getMovies(): Single<List<MovieEntity>>
    fun search(query: String): Single<List<MovieEntity>>
    fun getMovie(movieId: Int): Single<Optional<MovieEntity>>
}