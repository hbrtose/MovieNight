package com.yossisegev.movienight.di

import android.arch.persistence.room.Room
import com.squareup.picasso.Picasso
import com.yossisegev.data.db.MoviesDatabase
import com.yossisegev.data.db.RoomFavoritesMovieCache
import com.yossisegev.data.entities.DetailsData
import com.yossisegev.data.entities.MovieData
import com.yossisegev.data.mappers.DetailsDataMovieEntityMapper
import com.yossisegev.data.mappers.MovieDataEntityMapper
import com.yossisegev.data.mappers.MovieEntityDataMapper
import com.yossisegev.data.repositories.CachedMoviesDataStore
import com.yossisegev.data.repositories.MoviesRepositoryImpl
import com.yossisegev.data.repositories.RemoteMoviesDataStore
import com.yossisegev.domain.MoviesCache
import com.yossisegev.domain.MoviesRepository
import com.yossisegev.domain.common.Mapper
import com.yossisegev.domain.entities.MovieEntity
import com.yossisegev.domain.usecases.*
import com.yossisegev.movienight.MovieEntityMovieMapper
import com.yossisegev.movienight.common.ASyncTransformer
import com.yossisegev.movienight.common.ImageLoader
import com.yossisegev.movienight.common.PicassoImageLoader
import com.yossisegev.movienight.details.MovieDetailsViewModel
import com.yossisegev.movienight.entities.Movie
import com.yossisegev.movienight.favorites.FavoriteMoviesViewModel
import com.yossisegev.movienight.popularmovies.PopularMoviesViewModel
import com.yossisegev.movienight.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

object MapperKeys {
    const val DETAILS_TO_MOVIE_ENTITY = "details_to_movie_entity"
    const val MOVIE_DATA_TO_MOVIE_ENTITY = "movie_data_to_movie_entity"
    const val MOVIE_ENTITY_TO_MOVIE_DATA = "movie_entity_to_movie_data"
    const val MOVIE_ENTITY_TO_MOVIE = "movie_entity_to_movie"
}


val viewModels = module {
    viewModel { SearchViewModel(get(), get(MapperKeys.MOVIE_ENTITY_TO_MOVIE)) }
    viewModel { MovieDetailsViewModel(get(), get(), get(), get(), get(MapperKeys.MOVIE_ENTITY_TO_MOVIE)) }
    viewModel { FavoriteMoviesViewModel(get(), get(MapperKeys.MOVIE_ENTITY_TO_MOVIE)) }
    viewModel { PopularMoviesViewModel(get(), get(MapperKeys.MOVIE_ENTITY_TO_MOVIE)) }
}

val dataModule = module(createOnStart = true) {
    single<MoviesRepository> { MoviesRepositoryImpl(get(), get()) }
    single { CachedMoviesDataStore(get()) }
    single { RemoteMoviesDataStore(get()) }
    single<ImageLoader> { PicassoImageLoader(Picasso.with(androidContext())) }
    single<MoviesCache> { RoomFavoritesMovieCache(get(), get(MapperKeys.MOVIE_ENTITY_TO_MOVIE_DATA), get(MapperKeys.MOVIE_DATA_TO_MOVIE_ENTITY)) }
    single { Room.databaseBuilder(androidContext(),
            MoviesDatabase::class.java, "movies_db")
            .build() }
}

val useCases = module {
    factory { CheckFavoriteStatus(ASyncTransformer(), get()) }
    factory { GetFavoriteMovies(ASyncTransformer(), get()) }
    factory { GetMovieDetails(ASyncTransformer(), get()) }
    factory { GetPopularMovies(ASyncTransformer(), get()) }
    factory { RemoveFavoriteMovie(ASyncTransformer(), get()) }
    factory { SaveFavoriteMovie(ASyncTransformer(), get()) }
    factory { SearchMovie(ASyncTransformer(), get()) }
}

val mappers = module {
    factory<Mapper<DetailsData, MovieEntity>>(MapperKeys.DETAILS_TO_MOVIE_ENTITY) { DetailsDataMovieEntityMapper() }
    factory<Mapper<MovieData, MovieEntity>>(MapperKeys.MOVIE_DATA_TO_MOVIE_ENTITY) { MovieDataEntityMapper() }
    factory<Mapper<MovieEntity, MovieData>>(MapperKeys.MOVIE_ENTITY_TO_MOVIE_DATA) { MovieEntityDataMapper() }
    factory<Mapper<MovieEntity, Movie>>(MapperKeys.MOVIE_ENTITY_TO_MOVIE) { MovieEntityMovieMapper() }
}

