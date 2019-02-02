package com.yossisegev.data.repositories

import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.MoviesDataStore
import com.yossisegev.domain.entities.MovieEntity
import com.yossisegev.domain.entities.Optional
import io.reactivex.Single

/**
 * Created by Yossi Segev on 22/01/2018.
 */
class CachedMoviesDataStore(private val moviesCache: MoviesCache): MoviesDataStore {

    override fun search(query: String): Single<List<MovieEntity>> {
        return moviesCache.search(query)
    }

    override fun getMovieById(movieId: Int): Single<Optional<MovieEntity>> {
        return moviesCache.get(movieId)
    }

    override fun getMovies(): Single<List<MovieEntity>> {
        return moviesCache.getAll()
    }

    fun isEmpty(): Single<Boolean> {
        return moviesCache.isEmpty()
    }

    fun saveAll(movieEntities: List<MovieEntity>) {
        moviesCache.saveAll(movieEntities)
    }
}