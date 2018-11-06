package com.yossisegev.data.api

import com.squareup.moshi.Json
import com.yossisegev.data.entities.MovieData

/**
 * Created by Yossi Segev on 11/11/2017.
 */
class MovieListResult {

    var page: Int = 0
    @field:Json(name = "results")
    lateinit var movies: List<MovieData>
}