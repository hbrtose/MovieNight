package com.yossisegev.data.entities

import com.squareup.moshi.Json
import com.yossisegev.data.api.ReviewsResult
import com.yossisegev.data.api.VideoResult

/**
 * Created by Yossi Segev on 07/01/2018.
 */
data class DetailsData(

        @field:Json(name = "adult")
        var adult: Boolean = false,

//    @SerializedName("belongs_to_collection")
//    var belongsToCollection: Any? = null

        @field:Json(name = "budget")
        var budget: Int? = null,

        @field:Json(name = "genres")
        var genres: List<GenreData>? = null,

        @field:Json(name = "videos")
        var videos: VideoResult? = null,

        @field:Json(name = "reviews")
        var reviews: ReviewsResult? = null,

        @field:Json(name = "homepage")
        var homepage: String? = null,

        @field:Json(name = "id")
        var id: Int = -1,

        @field:Json(name = "imdb_id")
        var imdbId: String? = null,

        @field:Json(name = "popularity")
        var popularity: Double = 0.0,

//    @SerializedName("production_companies")
//    @Expose
//    var productionCompanies: List<ProductionCompany>? = null

//    @SerializedName("production_countries")
//    @Expose
//    var productionCountries: List<ProductionCountry>? = null

        @field:Json(name = "revenue")
        var revenue: Int? = null,

        @field:Json(name = "runtime")
        var runtime: Int? = null,

//    @SerializedName("spoken_languages")
//    @Expose
//    var spokenLanguages: List<SpokenLanguage>? = null

//    @SerializedName("status")
//    var status: String? = null

        @field:Json(name = "tagline")
        var tagline: String? = null,

        @field:Json(name = "video")
        var video: Boolean = false,

        @field:Json(name = "vote_average")
        var voteAverage: Double = 0.0,

        @field:Json(name = "vote_count")
        var voteCount: Int = 0,

        @field:Json(name = "title")
        var title: String,

        @field:Json(name = "poster_path")
        var posterPath: String,

        @field:Json(name = "original_language")
        var originalLanguage: String,

        @field:Json(name = "original_title")
        var originalTitle: String,

        @field:Json(name = "backdrop_path")
        var backdropPath: String,

        @field:Json(name = "overview")
        var overview: String,

        @field:Json(name = "release_date")
        var releaseDate: String
)