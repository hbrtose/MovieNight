package com.yossisegev.movienight.common

import com.yossisegev.domain.common.Transformer
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Yossi Segev on 11/11/2017.
 */

class ASyncTransformer<T> : Transformer<T>() {
    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}