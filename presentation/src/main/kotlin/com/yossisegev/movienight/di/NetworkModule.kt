package com.yossisegev.movienight.di

import android.content.Context
import com.yossisegev.data.api.Api
import com.yossisegev.movienight.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


val networkModule = module(createOnStart = true) {
    single { createHttpClient(androidContext()) }
    single { createApi<Api>(androidContext(), get()) }
}

fun createHttpClient(context: Context): OkHttpClient {
    val builder = OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
    provideInterceptors(context).forEach { builder.addInterceptor(it) }
    return builder.build()
}

inline fun <reified T> createApi(context: Context, okHttpClient: OkHttpClient): T {
    val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.api_base_url))
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    return retrofit.create(T::class.java)
}

fun provideInterceptors(context: Context): List<Interceptor> {
    val interceptors = arrayListOf<Interceptor>()
    val keyInterceptor = Interceptor {
        val original = it.request()
        val originalUrl = original.url()
        val url = originalUrl.newBuilder()
                .addQueryParameter("api_key", context.getString(R.string.api_key))
                .build()
        val request = original.newBuilder().url(url).build()
        return@Interceptor it.proceed(request)
    }
    interceptors.add(keyInterceptor)
    return interceptors
}