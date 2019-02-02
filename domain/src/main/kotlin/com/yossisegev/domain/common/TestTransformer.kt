package com.yossisegev.domain.common

import io.reactivex.*

/**
 * Created by Yossi Segev on 13/11/2017.
 */
class TestTransformer<T>: Transformer<T>() {
    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream
    }

}