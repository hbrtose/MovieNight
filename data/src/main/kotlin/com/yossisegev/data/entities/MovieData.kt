package com.yossisegev.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


/**
 * Created by Yossi Segev on 11/11/2017.
 */
@Entity(tableName = "movies")
data class MovieData(

        @field:Json(name = "id")
        @PrimaryKey
        var id: Int = -1,

        @field:Json(name = "vote_count")
        var voteCount: Int = 0,

        @field:Json(name = "vote_average")
        var voteAverage: Double = 0.0,

        @field:Json(name = "adult")
        var adult: Boolean = false,

        @field:Json(name = "popularity")
        var popularity: Double = 0.0,

        @field:Json(name = "title")
        var title: String,

        @field:Json(name = "poster_path")
        var posterPath: String? = null,

        @field:Json(name = "original_language")
        var originalLanguage: String,

        @field:Json(name = "original_title")
        var originalTitle: String,

        @field:Json(name = "backdrop_path")
        var backdropPath: String? = null,

        @field:Json(name = "release_date")
        var releaseDate: String,

        @field:Json(name = "overview")
        var overview: String? = null
)