package com.yossisegev.movienight.di

import com.yossisegev.data.api.Api
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiProperties {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val KEY = ""
}

val networkModule = module(createOnStart = true) {
    single { createHttpClient() }
    single { createApi<Api>(get()) }
}

fun createHttpClient(): OkHttpClient {
    val builder = OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
    provideInterceptors().forEach { builder.addInterceptor(it) }
    return builder.build()
}

inline fun <reified T> createApi(okHttpClient: OkHttpClient): T {
    val retrofit = Retrofit.Builder()
            .baseUrl(ApiProperties.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    return retrofit.create(T::class.java)
}

fun provideInterceptors(): List<Interceptor> {
    val interceptors = arrayListOf<Interceptor>()
    val keyInterceptor = Interceptor {
        val original = it.request()
        val originalUrl = original.url()
        val url = originalUrl.newBuilder()
                .addQueryParameter("api_key", ApiProperties.KEY)
                .build()
        val request = original.newBuilder().url(url).build()
        return@Interceptor it.proceed(request)
    }
    interceptors.add(keyInterceptor)
    return interceptors
}