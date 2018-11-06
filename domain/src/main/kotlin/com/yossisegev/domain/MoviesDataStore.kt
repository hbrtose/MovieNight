package com.yossisegev.domain

import com.yossisegev.domain.entities.MovieEntity
import com.yossisegev.domain.entities.Optional
import io.reactivex.Single

/**
 * Created by Yossi Segev on 11/11/2017.
 */
interface MoviesDataStore {

    fun getMovieById(movieId: Int): Single<Optional<MovieEntity>>
    fun getMovies(): Single<List<MovieEntity>>
    fun search(query: String): Single<List<MovieEntity>>
}