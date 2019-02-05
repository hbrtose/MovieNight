package com.yossisegev.domain.usecases

import com.yossisegev.domain.common.Transformer
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Yossi Segev on 11/11/2017.
 */
abstract class UseCase<P, T>(private val transformer: Transformer<T>) {

    abstract fun createObservable(data: P? = null): Single<T>

    fun observable(withData: P? = null): Single<T> {
        return createObservable(withData).compose(transformer)
    }

}