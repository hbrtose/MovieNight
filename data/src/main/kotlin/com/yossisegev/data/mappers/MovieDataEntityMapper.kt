package com.yossisegev.data.mappers

import com.yossisegev.data.entities.MovieData
import com.yossisegev.domain.common.Mapper
import com.yossisegev.domain.entities.MovieEntity

/**
 * Created by Yossi Segev on 11/11/2017.
 */
class MovieDataEntityMapper: Mapper<MovieData, MovieEntity>() {

    override fun mapFrom(from: MovieData): MovieEntity {
        return MovieEntity(
                id = from.id,
                voteCount = from.voteCount,
                voteAverage = from.voteAverage,
                popularity = from.popularity,
                adult = from.adult,
                title = from.title,
                posterPath = from.posterPath,
                originalLanguage = from.originalLanguage,
                backdropPath = from.backdropPath,
                originalTitle = from.originalTitle,
                releaseDate = from.releaseDate,
                overview = from.overview
        )
    }
}
