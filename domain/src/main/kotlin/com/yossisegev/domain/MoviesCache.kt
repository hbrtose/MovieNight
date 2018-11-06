package com.yossisegev.domain

import com.yossisegev.domain.entities.MovieEntity
import com.yossisegev.domain.entities.Optional
import io.reactivex.Single

/**
 * Created by Yossi Segev on 21/01/2018.
 */
interface MoviesCache {

    fun clear()
    fun save(movieEntity: MovieEntity)
    fun remove(movieEntity: MovieEntity)
    fun saveAll(movieEntities: List<MovieEntity>)
    fun getAll(): Single<List<MovieEntity>>
    fun get(movieId: Int): Single<Optional<MovieEntity>>
    fun search(query: String): Single<List<MovieEntity>>
    fun isEmpty(): Single<Boolean>
}