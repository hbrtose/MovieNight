package com.yossisegev.movienight.common

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.yossisegev.movienight.di.*
import org.koin.android.ext.android.startKoin

/**
 * Created by Yossi Segev on 11/11/2017.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)

        startKoin(this, listOf(viewModels, dataModule, networkModule, useCases, mappers))
    }
}